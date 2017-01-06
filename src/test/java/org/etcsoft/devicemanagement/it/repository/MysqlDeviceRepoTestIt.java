package org.etcsoft.devicemanagement.it.repository;

import lombok.SneakyThrows;
import org.etcsoft.devicemanagement.model.Device;
import org.etcsoft.devicemanagement.model.Exceptions.IllegalException;
import org.etcsoft.devicemanagement.model.User;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.etcsoft.devicemanagement.repository.Constants.NAMESPACE;
import static org.junit.Assert.fail;

public final class MysqlDeviceRepoTestIt extends MysqlAbstractRepo {

    @Test
    public void WhenInsertDevice_ThenOk() {
        Device device = createDevice();
        deviceRepo.insert(device);
        assertIfNotEqual(device);
    }

    @Test
    public void WhenInsertDeviceWithUsernames_ThenOk() {
        //generate users
        List<User> users = Arrays.asList(
                createUser(),
                createUser());
        //save user
        users.forEach(user ->
                saveUser(user));
        //generate device with users
        Device device = createDevice(
                users.stream().map(user ->
                        user.getUsername()).collect(Collectors.toList()));
        //insert using business class
        deviceRepo.insert(device);
        //checking db
        assertIfNotEqual(device);
    }

    @Test
    public void WhenUpdateDevice_ThenOk() {
        Device device = createDevice();
        saveDevice(device);
        Device deviceUpdated = Device
                .builder()
                .manufacture("manufactureUpdated")
                .build();
        deviceRepo.update(deviceUpdated, device.getDeviceId());
        assertIfNotEqual(mergeDevices(deviceUpdated, device));
    }

    @Test
    public void WhenDropDevice_ThenOk() {
        Device device = createDevice();
        saveDevice(device);
        deviceRepo.drop(device.getDeviceId().toString());
        Assert.assertNull(readDevice(device.getDeviceId().toString()));
    }

    @Test
    public void WhenIsExistsDevice_ThenOk() {
        Device device = createDevice();
        saveDevice(device);
        Assert.assertTrue(deviceRepo.isExists(device.getDeviceId().toString()));
        Assert.assertFalse(deviceRepo.isExists("Unknown"));
    }

    @Test
    public void WhenInsertDeviceWithDeviceNull_ThenException() {
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("Something was wrong during insert, error null");
        deviceRepo.insert(null);
    }

    @Test
    public void WhenInsertDeviceWithDeviceAlreadyExisting_ThenException() {
        Device device = createDevice();
        saveDevice(device);

        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage(
                format("Something was wrong during insert, error Duplicate entry '%s' for key 'PRIMARY'",
                        device.getDeviceId()));
        deviceRepo.insert(device);
    }

    @Test
    public void WhenInsertDeviceWithObjectNotExisting_ThenException() {
        Device device = createDevice(Arrays.asList("unknown"));

        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage(
                "Something was wrong during insert, error Cannot add or update a child row: a foreign key constraint fails (`device_management`.`owner_device_relationship`, CONSTRAINT `owner_device_relationship_ibfk_2` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE)");
        deviceRepo.insert(device);
    }

    @Test
    public void WhenUpdateDeviceWithDeviceNull_ThenException() {
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("Something was wrong during update, error null");
        deviceRepo.update(null, UUID.randomUUID());
    }

    @Test
    public void WhenUpdateDeviceWithUnknowndevice_ThenException() {
        Device deviceUpdated = Device
                .builder()
                .manufacture("manufactureUpdated")
                .build();
        deviceRepo.update(deviceUpdated, UUID.randomUUID());
        Assert.assertNull(readDevice(UUID.randomUUID().toString()));
    }

    @Test
    public void WhenUpdatedeviceWithDeviceIdNull_ThenException() {
        Device device = createDevice();
        saveDevice(device);
        Device deviceUpdated = Device
                .builder()
                .manufacture("manufactureUpdated")
                .build();
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("Something was wrong during update, error null");
        deviceRepo.update(deviceUpdated, null);
    }

    @SneakyThrows
    private Device readDevice(String deviceId) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                PreparedStatement selectStatement = connection.prepareStatement(
                        "SELECT * FROM " + NAMESPACE + ".device WHERE device_id = ?");
                selectStatement.setString(1, deviceId);
                final ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    return Device
                            .builder()
                            .partNumber(resultSet.getString("part_number"))
                            .manufacture(resultSet.getString("manufacture"))
                            .fwVersion(resultSet.getString("fw_version"))
                            .deviceId(UUID.fromString(deviceId))
                            .lasUpdate(new DateTime(resultSet.getTimestamp("last_update")))
                            .owners(getOwners(deviceId))
                            .properties(getProperties(deviceId))
                            .build();
                }
                return null;
            } catch (SQLException ex) {
                fail();
                return null;
            }
        }
    }

    private void assertIfNotEqual(Device expected) {
        final Device actual = readDevice(expected.getDeviceId().toString());
        Assert.assertEquals(expected.getDeviceId(), actual.getDeviceId());
        Assert.assertEquals(expected.getFwVersion(), actual.getFwVersion());
        Assert.assertEquals(expected.getLasUpdate(), actual.getLasUpdate());
        Assert.assertEquals(expected.getManufacture(), actual.getManufacture());
        Assert.assertEquals(expected.getPartNumber(), actual.getPartNumber());
        Assert.assertArrayEquals(expected.getOwners().toArray(), actual.getOwners().toArray());
        Assert.assertEquals(expected.getProperties(), actual.getProperties());
    }

    @SneakyThrows
    private List<String> getOwners(String deviceId) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                final List<String> usernames = new ArrayList<>();

                PreparedStatement selectStatement = connection.prepareStatement(
                        "SELECT username FROM " + NAMESPACE + ".owner_device_relationship WHERE device_id = ?");
                selectStatement.setString(1, deviceId);
                final ResultSet resultSet = selectStatement.executeQuery();
                while (resultSet.next()) {
                    usernames.add(resultSet.getString("username"));
                }
                return usernames;
            } catch (SQLException ex) {
                fail();
                return new ArrayList<>();
            }
        }
    }

    @SneakyThrows
    private Map<String, Object> getProperties(String deviceId) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                final Map<String, Object> properties = new HashMap<>();

                PreparedStatement selectStatement = connection.prepareStatement(
                        "SELECT property_name, value  FROM " + NAMESPACE + ".properties WHERE device_id = ?");
                selectStatement.setString(1, deviceId);
                final ResultSet resultSet = selectStatement.executeQuery();
                while (resultSet.next()) {
                    properties.put(resultSet.getString("property_name"), resultSet.getObject("value"));
                }
                return properties;
            } catch (SQLException ex) {
                fail();
                return new HashMap<>();
            }
        }
    }

    private Device mergeDevices(Device deviceA, Device deviceB) {
        return Device
                .builder()
                .deviceId(deviceA.getDeviceId() == null ? deviceB.getDeviceId() : deviceA.getDeviceId())
                .lasUpdate(deviceA.getLasUpdate() == null ? deviceB.getLasUpdate() : deviceA.getLasUpdate())
                .fwVersion(deviceA.getFwVersion() == null ? deviceB.getFwVersion() : deviceA.getFwVersion())
                .manufacture(deviceA.getManufacture() == null ? deviceB.getManufacture() : deviceA.getManufacture())
                .partNumber(deviceA.getPartNumber() == null ? deviceB.getPartNumber() : deviceA.getPartNumber())
                .owners(new ArrayList<String>() {{
                    addAll(deviceA.getOwners());
                    addAll(deviceB.getOwners());
                }})
                .properties(new HashMap<String, Object>() {{
                    putAll(deviceA.getProperties());
                    putAll(deviceB.getProperties());
                }})
                .build();
    }
}
