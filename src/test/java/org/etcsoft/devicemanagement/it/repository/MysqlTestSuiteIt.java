package org.etcsoft.devicemanagement.it.repository;

import lombok.SneakyThrows;
import org.etcsoft.dockertest.docker.DockerItFactory;
import org.etcsoft.dockertest.docker.MysqlDockerIt;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.Arrays;

import static org.etcsoft.devicemanagement.repository.Constants.NAMESPACE;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MysqlDeviceRepoItTest.class,
        MysqlUserRepoItTest.class,
        MysqlUserDeviceLinkRepoItTest.class})
public final class MysqlTestSuiteIt {
    private static MysqlDockerIt mysqlUtils = DockerItFactory.getDefaultMysqlInstance();
    public MysqlTestSuiteIt() {}

    @BeforeClass
    public static void testSuiteSetup() {
        mysqlUtils.loadSchema(Arrays.asList(
                String.format("CREATE DATABASE %s", NAMESPACE),
                String.format("use %s", NAMESPACE),
                "CREATE TABLE user(" +
                        " username VARCHAR(50) NOT NULL," +
                        " email VARCHAR(100) NOT NULL," +
                        " password VARCHAR(100) DEFAULT NULL," +
                        " first_name VARCHAR(50) DEFAULT NULL," +
                        " last_name VARCHAR(50) DEFAULT NULL," +
                        " PRIMARY KEY ( username ) )",
                "CREATE TABLE device(" +
                        " device_id VARCHAR(40) NOT NULL," +
                        " fw_version VARCHAR(50) NOT NULL," +
                        " last_update DATETIME DEFAULT NULL," +
                        " manufacture VARCHAR(50) DEFAULT NULL," +
                        " part_number VARCHAR(50) NOT NULL," +
                        " PRIMARY KEY ( device_id ) )",
                "CREATE TABLE properties(" +
                        " id MEDIUMINT NOT NULL AUTO_INCREMENT," +
                        " device_id VARCHAR(40) NOT NULL," +
                        " property_name VARCHAR(50) NOT NULL," +
                        " value BLOB DEFAULT NULL," +
                        " PRIMARY KEY ( id ), " +
                        " FOREIGN KEY (device_id) REFERENCES device (device_id) " +
                        " ON DELETE CASCADE " +
                        " ON UPDATE CASCADE )",
                "CREATE TABLE owner_device_relationship(" +
                        " id MEDIUMINT NOT NULL AUTO_INCREMENT," +
                        " device_id VARCHAR(40) NOT NULL," +
                        " username VARCHAR(50) NOT NULL," +
                        " PRIMARY KEY ( id ), " +
                        " FOREIGN KEY (device_id) REFERENCES device (device_id) " +
                        " ON DELETE CASCADE " +
                        " ON UPDATE CASCADE, " +
                        " FOREIGN KEY (username) REFERENCES user (username) " +
                        " ON DELETE CASCADE " +
                        " ON UPDATE CASCADE )"
        ));
    }

    @AfterClass
    @SneakyThrows
    public static void tearDownTest() {
        mysqlUtils.close();
    }
}
