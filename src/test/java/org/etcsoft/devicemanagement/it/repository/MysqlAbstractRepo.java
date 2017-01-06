package org.etcsoft.devicemanagement.it.repository;

import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.etcsoft.devicemanagement.model.Device;
import org.etcsoft.devicemanagement.model.User;
import org.etcsoft.devicemanagement.repository.*;
import org.etcsoft.dockertest.docker.DockerItFactory;
import org.etcsoft.dockertest.docker.MysqlDockerIt;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.*;

import static java.lang.String.format;
import static org.etcsoft.devicemanagement.repository.Constants.NAMESPACE;
import static org.junit.Assert.fail;

public abstract class MysqlAbstractRepo {
    protected final HikariDataSource dataSource;
    protected final static MysqlDockerIt mysqlUtils = DockerItFactory.getDefaultMysqlInstance();
    protected final DeviceRepo deviceRepo;
    protected final UserDeviceLinkRepo userDeviceLinkRepo;
    protected final UserRepo userRepo;

    @Rule
        public final ExpectedException thrownExpected = ExpectedException.none();

    public MysqlAbstractRepo() {
        this.dataSource = mysqlUtils.getDatasource();
        deviceRepo = new MysqlDeviceRepo(dataSource);
        userDeviceLinkRepo = new MysqlUserDeviceLinkRepo(dataSource);
        userRepo = new MysqlUserRepo(dataSource);
    }

    public User createUser() {
        return createUser(new ArrayList<>());
    }

    public User createUser(List<UUID> deviceIds) {
        final String username = format("username%s", generatePrefix());
        return User
                .builder()
                .username(username)
                .lastName("lastname")
                .passwd("password")
                .email(format("%s@email.com", username))
                .firstName("firstName")
                .devices(deviceIds)
                .build();
    }

    @SneakyThrows
    public void saveUser(User user) {
        try (Connection connection =
                     dataSource.getConnection()) {
            connection.setAutoCommit(true);

            final PreparedStatement insertUser = connection.prepareStatement(
                    "INSERT INTO " + NAMESPACE + ".user " +
                            "(username, email, password, first_name, last_name) " +
                            "VALUES (?, ?, ?, ?, ?)");
            insertUser.setString(1, user.getUsername());
            insertUser.setString(2, user.getEmail());
            insertUser.setString(3, BCrypt.hashpw(user.getPasswd(), BCrypt.gensalt(12)));
            insertUser.setString(4, user.getFirstName());
            insertUser.setString(5, user.getLastName());
            insertUser.execute();

            final PreparedStatement insertOwnerLinks = connection.prepareStatement(
                    "INSERT INTO " + NAMESPACE + ".owner_device_relationship (device_id, username) VALUES (?, ?)");
            for(UUID deviceId : user.getDevices()) {
                insertOwnerLinks.setString(1, deviceId.toString());
                insertOwnerLinks.setString(2, user.getUsername());
                insertOwnerLinks.addBatch();
            }
            insertOwnerLinks.executeBatch();
        } catch (Exception ex) {
            fail();
        }
    }

    public Device createDevice() {
        return createDevice(new ArrayList<>(), new HashMap<>());
    }

    public Device createDevice(List<String> usernames) {
        return createDevice(usernames, new HashMap<>());
    }

    public Device createDevice(Map<String, Object> properties) {
        return createDevice(new ArrayList<>(), properties);
    }

    public Device createDevice(List<String> usernames, Map<String, Object> properties) {
        final UUID deviceId = UUID.randomUUID();
        return Device
                .builder()
                .deviceId(deviceId)
                .lasUpdate(DateTime.now().withMillisOfSecond(0))
                .fwVersion("v1.0.1")
                .manufacture("home")
                .partNumber("55555")
                .properties(properties)
                .owners(usernames)
                .build();
    }

    @SneakyThrows
    public void saveDevice(Device device) {
        Class.forName("com.mysql.jdbc.Driver");
        try (Connection connection =
                     dataSource.getConnection()) {
            connection.setAutoCommit(true);

            final PreparedStatement insertDevice = connection.prepareStatement(
                    "INSERT INTO " + NAMESPACE + ".device " +
                            "(device_id, fw_version, last_update, manufacture, part_number) " +
                            "VALUES (?, ?, ?, ?, ?)");
            insertDevice.setString(1, device.getDeviceId().toString());
            insertDevice.setString(2, device.getFwVersion());
            insertDevice.setTimestamp(3, new Timestamp(device.getLasUpdate().getMillis()));
            insertDevice.setString(4, device.getManufacture());
            insertDevice.setString(5, device.getPartNumber());
            insertDevice.execute();

            final PreparedStatement insertProperties = connection.prepareStatement(
                    "INSERT INTO " + NAMESPACE + ".properties (device_id, property_name, value) VALUES (?, ?, ?)");
            for(Map.Entry<String, Object> property : device.getProperties().entrySet()) {
                insertProperties.setString(1, device.getDeviceId().toString());
                insertProperties.setString(2, property.getKey());
                insertProperties.setObject(3, property.getValue());
                insertProperties.addBatch();
            }
            insertProperties.executeBatch();

            final PreparedStatement insertOwnerLinks = connection.prepareStatement(
                    "INSERT INTO " + NAMESPACE + ".owner_device_relationship (device_id, username) VALUES (?, ?)");
            for(String owner : device.getOwners()) {
                insertOwnerLinks.setString(1, device.getDeviceId().toString());
                insertOwnerLinks.setString(2, owner);
                insertOwnerLinks.addBatch();
            }
            insertOwnerLinks.executeBatch();
        } catch (Exception ex) {
            fail();
        }
    }

    @SneakyThrows
    public void linkOwnerDevice(String username, UUID deviceId) {
        Class.forName("com.mysql.jdbc.Driver");
        try (Connection connection =
                     dataSource.getConnection()) {
            connection.setAutoCommit(true);

            final PreparedStatement insertOwnerLinks = connection.prepareStatement(
                    "INSERT INTO " + NAMESPACE + ".owner_device_relationship (device_id, username) VALUES (?, ?)");
            insertOwnerLinks.setString(1, deviceId.toString());
            insertOwnerLinks.setString(2, username);
            insertOwnerLinks.execute();
        } catch (Exception ex) {
            fail();
        }
    }

    private String generatePrefix() {
        final Random rn = new Random();
        return String.valueOf(rn.nextInt(1000000 + 1));
    }
}
