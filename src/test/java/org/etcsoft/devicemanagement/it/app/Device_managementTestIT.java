package org.etcsoft.devicemanagement.it.app;

import lombok.SneakyThrows;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class Device_managementTestIT {
//    private static MysqlDockerIt mysqlUtils = DockerItFactory.getMysqlInstance();
//    private HikariDataSource hikariDatasource = mysqlUtils.getDatasource();
//    private DeviceRepo deviceRepo = new MysqlDeviceRepo(hikariDatasource);
//    private UserRepo userRepo = new MysqlUserRepo(hikariDatasource);
//    private UserDeviceLinkRepo userDeviceLinkRepo = new MysqlUserDeviceLinkRepo(hikariDatasource);
//
//    private final static String APP_IMAGE_NAME = "device_management";
//    private static String myChallengeApp;
//
//    @Rule
//    public final ExpectedException thrownExpected = ExpectedException.none();

    @BeforeClass
    @SneakyThrows
    public static void setup() {
//        myChallengeApp = startAndWait4AppImage();
//        configureRestAssured();
    }

    @AfterClass
    @SneakyThrows
    public static void cleanup() {
//        if (dockerClient != null) {
//            if (mysqlContainerId != null) {
//                dockerClient.stopContainer(mysqlContainerId, 20);
//                dockerClient.removeContainer(mysqlContainerId);
//            }
//            if (myChallengeApp != null) {
//                dockerClient.stopContainer(myChallengeApp, 20);
//                dockerClient.removeContainer(myChallengeApp);
//            }
//            dockerClient.close();
//        }
//        mockServer.stop();
//        mockServer.shutdown();
    }
}
