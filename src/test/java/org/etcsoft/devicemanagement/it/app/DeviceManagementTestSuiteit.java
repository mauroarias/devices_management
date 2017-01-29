package org.etcsoft.devicemanagement.it.app;

import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.eclipse.jetty.http.HttpStatus;
import org.etcsoft.devicemanagement.repository.*;
import org.etcsoft.dockertest.docker.DockerContainerFactory;
import org.etcsoft.dockertest.docker.MysqlDocker;
import org.etcsoft.dockertest.docker.SpringDockerApp;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.lang.String.format;
import static org.etcsoft.dockertest.docker.DockerConstants.DOCKER_OAUTH2_CONTAINER_ALIAS;
import static org.etcsoft.dockertest.docker.DockerConstants.DOCKER_OAUTH2_MYSQL_NAME;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DeviceManagementUserItTest.class})
public class DeviceManagementTestSuiteit {
    private final static String APP_IMAGE_NAME = "devicemanagement";
    private final static String APP_FULL_IMAGE_NAME = "mauroarias/" + APP_IMAGE_NAME;
    private static final String AUTH_USERNAME = "my-client-with-secret";
    private static final String AUTH_PASSWORD = "secret";
    private static final String APPLICATION_CONTEXT = "application/x-www-form-urlencoded";
    private static final String GRANT_TYPE_CLIENT_CREDENTIAL_READ = "grant_type=client_credentials&scope=read";
    private static final String URI = "http://localhost";
    private static final String OAUTH2_PORT = "8081";
    private static final String PORT = "8082";

    private static final List<String> deviceManagementSchema = Arrays.asList(
            "create database device_management;",
            "use device_management;",
            "CREATE TABLE user " +
                    "(" +
                    "username VARCHAR(50) NOT NULL," +
                    "email VARCHAR(100) NOT NULL," +
                    "password VARCHAR(100) DEFAULT NULL," +
                    "first_name VARCHAR(50) DEFAULT NULL," +
                    "last_name VARCHAR(50) DEFAULT NULL," +
                    "PRIMARY KEY ( username )" +
                    ");",
            "CREATE TABLE device " +
                    "(" +
                    "device_id VARCHAR(40) NOT NULL," +
                    "fw_version VARCHAR(50) NOT NULL," +
                    "last_update DATETIME DEFAULT NULL," +
                    "manufacture VARCHAR(50) DEFAULT NULL," +
                    "part_number VARCHAR(50) NOT NULL," +
                    "PRIMARY KEY ( device_id )" +
                    ");",
            "CREATE TABLE properties " +
                    "(" +
                    "id MEDIUMINT NOT NULL AUTO_INCREMENT," +
                    "device_id VARCHAR(40) NOT NULL," +
                    "property_name VARCHAR(50) NOT NULL," +
                    "value BLOB DEFAULT NULL," +
                    "PRIMARY KEY ( id )," +
                    "FOREIGN KEY (device_id) REFERENCES device (device_id) " +
                    "ON DELETE CASCADE " +
                    "ON UPDATE CASCADE" +
                    ");",
            "CREATE TABLE owner_device_relationship " +
                    "(" +
                    "id MEDIUMINT NOT NULL AUTO_INCREMENT," +
                    "device_id VARCHAR(40) NOT NULL," +
                    "username VARCHAR(50) NOT NULL," +
                    "PRIMARY KEY ( id )," +
                    "FOREIGN KEY (device_id) REFERENCES device (device_id) " +
                    "ON DELETE CASCADE " +
                    "ON UPDATE CASCADE," +
                    "FOREIGN KEY (username) REFERENCES user (username) " +
                    "ON DELETE CASCADE " +
                    "ON UPDATE CASCADE" +
                    ");"
    );

    private static SpringDockerApp oauth2Server;
    private static MysqlDocker mysqlContainer;
    private static SpringDockerApp deviceManagementApp;
    private static HikariDataSource hikariDatasource;
    private static DeviceRepo deviceRepo;
    private static UserRepo userRepo;
    private static UserDeviceLinkRepo userDeviceLinkRepo;

    public static String token;
    public static String uriPath;

    @BeforeClass
    public static void testSuiteSetup() {
        oauth2Server = DockerContainerFactory.getOauth2ServerBuilder().build();
        mysqlContainer = (MysqlDocker)oauth2Server.getParentContainers().get(DOCKER_OAUTH2_MYSQL_NAME);
        mysqlContainer.loadSchema(deviceManagementSchema);
        deviceManagementApp = DockerContainerFactory
                .getSpringAppBuilder()
                .imageName(APP_FULL_IMAGE_NAME)
                .addContainerLink(mysqlContainer.getContainerId(), "mysqlhost")
                .addContainerLink(mysqlContainer.getContainerId(), DOCKER_OAUTH2_CONTAINER_ALIAS)
                .addExposedPort(PORT)
                .build();
        hikariDatasource = mysqlContainer.getDatasource();
        deviceRepo = new MysqlDeviceRepo(hikariDatasource);
        userRepo = new MysqlUserRepo(hikariDatasource);
        userDeviceLinkRepo = new MysqlUserDeviceLinkRepo(hikariDatasource);

        uriPath = format("%s:%s/", URI, deviceManagementApp.getExposedRandomPort(PORT));
        token = given()
                .log().ifValidationFails()
                .log().all()
                .auth().basic(AUTH_USERNAME, AUTH_PASSWORD)
                .header("Content-Type", APPLICATION_CONTEXT)
                .body(GRANT_TYPE_CLIENT_CREDENTIAL_READ)
                .when()
                .post(format("%s:%s/%s", URI, oauth2Server.getExposedRandomPort(OAUTH2_PORT), "oauth/token"))
                .then()
                .contentType(JSON)
                .statusCode(HttpStatus.OK_200)
                .extract()
                .path("access_token");
    }

    @AfterClass
    @SneakyThrows
    public static void cleanup() {
        if (deviceManagementApp != null) {
            deviceManagementApp.close();
        }
        if (oauth2Server != null) {
            oauth2Server.close();
        }
    }
}
