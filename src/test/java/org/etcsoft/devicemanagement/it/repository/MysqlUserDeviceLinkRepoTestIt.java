package org.etcsoft.devicemanagement.it.repository;

import lombok.SneakyThrows;
import org.etcsoft.devicemanagement.model.Device;
import org.etcsoft.devicemanagement.model.Exceptions.IllegalException;
import org.etcsoft.devicemanagement.model.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

import static org.etcsoft.devicemanagement.repository.Constants.NAMESPACE;
import static org.junit.Assert.fail;

public final class MysqlUserDeviceLinkRepoTestIt extends MysqlAbstractRepo {

    @Test
    public void whenUserDeviceLink_ThenOk() {
        User user = createUser();
        saveUser(user);
        Device device = createDevice();
        saveDevice(device);
        userDeviceLinkRepo.linkDevicesToUser(device.getDeviceId(), user.getUsername());
    }

    @Test
    public void whenUserDeviceUnlink_ThenOk() {
        User user = createUser();
        saveUser(user);
        Device device = createDevice();
        saveDevice(device);
        userDeviceLink(device.getDeviceId(), user.getUsername());
        userDeviceLinkRepo.unLinkDevicesToUser(device.getDeviceId(), user.getUsername());
    }

    @Test
    public void whenUserDeviceLinkWithDeviceNull_ThenException() {
        User user = createUser();
        saveUser(user);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("Something was wrong during insert device ids into an username, error null");
        userDeviceLinkRepo.linkDevicesToUser(null, user.getUsername());
    }

    @Test
    public void whenUserDeviceUnlinkWithDeviceNull_ThenException() {
        User user = createUser();
        saveUser(user);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("Something was wrong during delete device ids into an username, error null");
        userDeviceLinkRepo.unLinkDevicesToUser(null, user.getUsername());
    }

    @SneakyThrows
    private void userDeviceLink(UUID deviceId, String username) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                final PreparedStatement insertOwnerLinks = connection.prepareStatement(
                        "INSERT INTO " + NAMESPACE + ".owner_device_relationship (device_id, username) VALUES (?, ?)");
                insertOwnerLinks.setString(1, deviceId.toString());
                insertOwnerLinks.setString(2, username);
                insertOwnerLinks.addBatch();

                connection.commit();

            } catch (Exception ex) {
                fail();
            }
        }

    }
}
