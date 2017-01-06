package org.etcsoft.devicemanagement.services;

import org.etcsoft.devicemanagement.model.Device;
import org.etcsoft.devicemanagement.model.Exceptions.IllegalException;
import org.etcsoft.devicemanagement.repository.DeviceRepo;
import org.etcsoft.devicemanagement.repository.UserDeviceLinkRepo;
import org.etcsoft.devicemanagement.repository.UserRepo;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.UUID;

import static java.lang.String.format;
import static java.util.Optional.of;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class DefaultDeviceServiceTest {
    private final UserRepo userRepo = mock(UserRepo.class);
    private final DeviceRepo deviceRepo = mock(DeviceRepo.class);
    private final UserDeviceLinkRepo linkRepo = mock(UserDeviceLinkRepo.class);
    private final DeviceService deviceService = new DefaultDeviceService(deviceRepo, linkRepo, userRepo);
    private final UUID deviceId = UUID.randomUUID();
    private final Device defaultDevice = Device
            .builder()
            .owner("username")
            .property("myProperty", 55)
            .partNumber("partNumber")
            .deviceId(deviceId)
            .fwVersion("V1.0.1")
            .lasUpdate(DateTime.now())
            .build();

    @Rule
    public final ExpectedException thrownExpected = ExpectedException.none();

    @Test
    public void whenCreateDevice_ThenOk() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(false);
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        Device deviceCreated = deviceService.create(defaultDevice);
        Assert.assertEquals(defaultDevice, deviceCreated);
    }

    @Test
    public void whenLinkUserDevice_thenOk() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        deviceService.linkUser("username", deviceId);
    }

    @Test
    public void whenUnlinkUserDevice_thenOk() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        deviceService.unLinkUser("username", deviceId);
    }

    @Test
    public void whenUpdateDevice_ThenOk() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        Device device = Device
                .builder()
                .owner("username")
                .property("myProperty2", "myString")
                .property("myProperty", 88)
                .partNumber("partNumber")
                .fwVersion("V1.0.1")
                .lasUpdate(DateTime.now())
                .build();
        deviceService.update(device, deviceId);
    }

    @Test
    public void whenDeleteDevice_ThenOk() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        deviceService.delete(deviceId);
    }

    @Test
    public void whenGetDevice_ThenOk() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        when(deviceRepo.select(any(String.class))).thenReturn(of(defaultDevice));
        Device userRead = deviceService.get(deviceId);
        Assert.assertSame(defaultDevice, userRead);
    }

    @Test
    public void whenCreateDeviceWithDeviceNull_ThenException() {
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("Device body cannot be null");
        deviceService.create(null);
    }

    @Test
    public void whenCreateDeviceWithExistingDeviceId_ThenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage(format("The device_id '%s' already exist", deviceId));
        deviceService.create(defaultDevice);
    }

    @Test
    public void whenCreateDeviceWithNotExistingDeviceID_ThenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(false);
        when(userRepo.isExists(any(String.class))).thenReturn(false);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage(format("The username 'username' linked to the device_Id '%s' does not exist", deviceId));
        deviceService.create(defaultDevice);
    }

    @Test
    public void whenUpdateDeviceWithNotExistingDeviceId_ThenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(false);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage(format("The device_id '%s' does not exist", deviceId));
        deviceService.update(defaultDevice, deviceId);
    }

    @Test
    public void whenUpdateDeviceWithDeviceNull_ThenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("Device body cannot be null");
        deviceService.update(null, deviceId);
    }

    @Test
    public void whenUpdateDeviceWithDeviceIdNull_ThenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("device_Id cannot be null");
        deviceService.update(defaultDevice, null);
    }

    @Test
    public void whenDeleteDeviceWithDeviceIdNull_ThenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("device_Id cannot be null");
        deviceService.delete(null);
    }

    @Test
    public void whenDeleteDeviceWithNotExistsDeviceId_ThenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(false);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage(format("The device_id '%s' does not exist",deviceId));
        deviceService.delete(deviceId);
    }

    @Test
    public void whenGetDeviceWithUsernameNull_ThenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("device_Id cannot be null");
        deviceService.get(null);
    }

    @Test
    public void whenGetDeviceWithNotExists_ThenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(false);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage(format("The device_id '%s' does not exist",deviceId));
        deviceService.get(deviceId);
    }

    @Test
    public void whenLinkUserDeviceWithNotExistDevice_thenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(false);
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage(format("The device_id '%s' does not exist",deviceId));
        deviceService.linkUser("username", deviceId);
    }

    @Test
    public void whenLinkUserDeviceWithNotExistUser_thenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        when(userRepo.isExists(any(String.class))).thenReturn(false);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("The username 'username' does not exist");
        deviceService.linkUser("username", deviceId);
    }

    @Test
    public void whenLinkUserDeviceWithEmptyUsername_thenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("username cannot be null or empty");
        deviceService.linkUser("", deviceId);
    }

    @Test
    public void whenLinkUserDeviceWithNullUsername_thenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("username cannot be null or empty");
        deviceService.linkUser(null, deviceId);
    }

    @Test
    public void whenLinkUserDeviceWithNullDeviceId_thenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("device_Id cannot be null");
        deviceService.linkUser("username", null);
    }

    @Test
    public void whenUnlinkUserDeviceWithNotExistDevice_thenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(false);
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage(format("The device_id '%s' does not exist",deviceId));
        deviceService.unLinkUser("username", deviceId);
    }

    @Test
    public void whenUnlinkUserDeviceWithNotExistUser_thenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        when(userRepo.isExists(any(String.class))).thenReturn(false);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("The username 'username' does not exist");
        deviceService.unLinkUser("username", deviceId);
    }

    @Test
    public void whenUnlinkUserDeviceWithEmptyUsername_thenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("username cannot be null or empty");
        deviceService.unLinkUser("", deviceId);
    }

    @Test
    public void whenUnlinkUserDeviceWithNullUsername_thenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("username cannot be null or empty");
        deviceService.unLinkUser(null, deviceId);
    }

    @Test
    public void whenUnlinkUserDeviceWithNullDeviceId_thenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("device_Id cannot be null");
        deviceService.unLinkUser("username", null);
    }
}
