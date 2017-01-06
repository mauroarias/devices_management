package org.etcsoft.devicemanagement.it.repository;

import lombok.SneakyThrows;
import org.etcsoft.devicemanagement.model.Device;
import org.etcsoft.devicemanagement.model.Exceptions.IllegalException;
import org.etcsoft.devicemanagement.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.etcsoft.devicemanagement.repository.Constants.NAMESPACE;
import static org.junit.Assert.fail;

public final class MysqlUserRepoTestIt extends MysqlAbstractRepo {

    @Test
    public void WhenInsertUser_ThenOk() {
        User user = createUser();
        userRepo.insert(user);
        assertIfNotEqual(user);
    }

    @Test
    public void WhenInsertUserWithDeviceIds_ThenOk() {
        //generate devices
        List<Device> devices = Arrays.asList(
                createDevice(),
                createDevice());
        //save devices
        devices.forEach(device ->
                saveDevice(device));
        //generate user with devices
        User user = createUser(
                devices.stream().map(device ->
                        device.getDeviceId()).collect(Collectors.toList()));
        //insert using business class
        userRepo.insert(user);
        //checking db
        assertIfNotEqual(user);
    }

    @Test
    public void WhenUpdateUser_ThenOk() {
        User user = createUser();
        saveUser(user);
        User userUpdated = User
                .builder()
                .firstName("firstUpdated")
                .build();
        userRepo.update(userUpdated, user.getUsername());
        assertIfNotEqual(mergeUser(userUpdated, user));
    }

    @Test
    public void WhenDropUser_ThenOk() {
        User user = createUser();
        saveUser(user);
        userRepo.drop(user.getUsername());
        Assert.assertNull(readUser(user.getUsername()));
    }

    @Test
    public void WhenUpdatePassword_ThenOk() {
        User user = createUser();
        saveUser(user);
        userRepo.updatePassword(user.getUsername(), "newPassword");
        assertIfNotEqual(
                mergeUser(
                        User
                                .builder()
                                .passwd("newPassword")
                                .build(),
                        user));
    }

    @Test
    public void WhenIsExistsUser_ThenOk() {
        User user = createUser();
        saveUser(user);
        Assert.assertTrue(userRepo.isExists(user.getUsername()));
        Assert.assertFalse(userRepo.isExists("Unknown"));
    }

    @Test
    public void WhenInsertUserWithUserNull_ThenException() {
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("Something was wrong during insert, error null");
        userRepo.insert(null);
    }

    @Test
    public void WhenInsertUserWithUserAlreadyExisting_ThenException() {
        User user = createUser();
        saveUser(user);

        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage(
                format("Something was wrong during insert, error Duplicate entry '%s' for key 'PRIMARY'",
                        user.getUsername()));
        userRepo.insert(user);
    }

    @Test
    public void WhenInsertUserWithDeviceNotExisting_ThenException() {
        User user = createUser(Arrays.asList(UUID.randomUUID()));

        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage(
                "Something was wrong during insert, error Cannot add or update a child row: a foreign key constraint fails (`device_management`.`owner_device_relationship`, CONSTRAINT `owner_device_relationship_ibfk_1` FOREIGN KEY (`device_id`) REFERENCES `device` (`device_id`) ON DELETE CASCADE ON UPDATE CASCADE)");
        userRepo.insert(user);
    }

    @Test
    public void WhenUpdateUserWithUserNull_ThenException() {
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("Something was wrong during update, error null");
        userRepo.update(null, "any");
    }

    @Test
    public void WhenUpdateUserWithUnknownUser_ThenException() {
        User userUpdated = User
                .builder()
                .firstName("firstUpdated")
                .build();
        userRepo.update(userUpdated, "unknown");
        Assert.assertNull(readUser("unknown"));
    }

    @Test
    public void WhenUpdateUserWithUsernameNull_ThenException() {
        User user = createUser();
        saveUser(user);
        User userUpdated = User
                .builder()
                .firstName("firstUpdated")
                .build();
        userRepo.update(userUpdated, null);
    }

    @SneakyThrows
    private User readUser(String username) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                PreparedStatement selectStatement = connection.prepareStatement(
                        "SELECT * FROM " + NAMESPACE + ".user  WHERE username = ?");
                selectStatement.setString(1, username);
                final ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    return User
                            .builder()
                            .username(username)
                            .email(resultSet.getString("email"))
                            .firstName(resultSet.getString("first_name"))
                            .lastName(resultSet.getString("last_name"))
                            .devices(getDevices(username))
                            .passwd(resultSet.getString("password"))
                            .build();
                }
                return null;
            } catch (SQLException ex) {
                fail();
                return null;
            }
        }
    }

    private void assertIfNotEqual(User expected) {
        final User actual = readUser(expected.getUsername());
        Assert.assertEquals(expected.getUsername(), actual.getUsername());
        Assert.assertEquals(expected.getEmail(), actual.getEmail());
        Assert.assertEquals(expected.getFirstName(), actual.getFirstName());
        Assert.assertEquals(expected.getLastName(), actual.getLastName());
        Assert.assertTrue(BCrypt.checkpw(expected.getPasswd(), actual.getPasswd()));
        Assert.assertArrayEquals(expected.getDevices().toArray(), actual.getDevices().toArray());
    }

    @SneakyThrows
    private List<UUID> getDevices(String username) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                final List<UUID> deviceIds = new ArrayList<>();

                PreparedStatement selectStatement = connection.prepareStatement(
                        "SELECT device_id FROM " + NAMESPACE + ".owner_device_relationship WHERE username = ?");
                selectStatement.setString(1, username);
                final ResultSet resultSet = selectStatement.executeQuery();
                while (resultSet.next()) {
                    deviceIds.add(UUID.fromString(resultSet.getString("device_id")));
                }
                return deviceIds;

            } catch (SQLException ex) {
                fail();
                return new ArrayList<>();
            }
        }
    }

    private User mergeUser(User userA, User userB) {
        return User
                .builder()
                .username(userA.getUsername() == null ? userB.getUsername() : userA.getUsername())
                .firstName(userA.getFirstName() == null ? userB.getFirstName() : userA.getFirstName())
                .lastName(userA.getLastName() == null ? userB.getLastName() : userA.getLastName())
                .email(userA.getEmail() == null ? userB.getEmail() : userA.getEmail())
                .passwd(userA.getPasswd() == null ? userB.getPasswd() : userA.getPasswd())
                .devices(new ArrayList<UUID>() {{
                    addAll(userA.getDevices());
                    addAll(userB.getDevices());
                }})
                .build();
    }
}
