package org.etcsoft.devicemanagement.services;

import org.apache.commons.lang3.StringUtils;
import org.etcsoft.devicemanagement.model.Device;
import org.etcsoft.devicemanagement.model.Exceptions.IllegalException;
import org.etcsoft.devicemanagement.repository.DeviceRepo;
import org.etcsoft.devicemanagement.repository.UserDeviceLinkRepo;
import org.etcsoft.devicemanagement.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static org.etcsoft.devicemanagement.model.Enums.ErrorCodes.*;

@Service
public final class DefaultDeviceService implements DeviceService {

    private final DeviceRepo deviceRepo;
    private final UserDeviceLinkRepo linkRepo;
    private final UserRepo userRepo;

    @Autowired
    public DefaultDeviceService(DeviceRepo deviceRepo, UserDeviceLinkRepo linkRepo, UserRepo userRepo) {
        this.deviceRepo = deviceRepo;
        this.linkRepo = linkRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Device create(Device device) {
        throwIfNull(device, "Device body cannot be null");
        throwIFDeviceExists(device.getDeviceId());
        throwIfUserListNotExists(device.getOwners(), device.getDeviceId());
        deviceRepo.insert(device);
        return device;
    }

    @Override
    public Device get(UUID deviceId) {
        throwIfNull(deviceId, "device_Id cannot be null");
        throwIFDeviceNotExists(deviceId);
        Optional<Device> device = deviceRepo.select(deviceId.toString());
        if(!device.isPresent()) {
            throw new IllegalException(
                    UNKNOWN_ERROR,
                    format("Device %s cannot be read from Database", deviceId.toString()));
        }
        return device.get();
    }

    @Override
    public void update(Device device, UUID DeviceID) {
        throwIfNull(device, "Device body cannot be null");
        throwIfNull(DeviceID, "device_Id cannot be null");
        throwIFDeviceNotExists(DeviceID);
        deviceRepo.update(device, DeviceID);
    }

    @Override
    public void delete(UUID DeviceID) {
        throwIfNull(DeviceID, "device_Id cannot be null");
        throwIFDeviceNotExists(DeviceID);
        deviceRepo.drop(DeviceID.toString());
    }

    @Override
    public void linkUser(String username, UUID deviceID) {
        throwIfNull(deviceID, "device_Id cannot be null");
        throwIfBlank(username, "username cannot be null or empty");
        throwIFDeviceNotExists(deviceID);
        throwIFUserNotExists(username);
        linkRepo.linkDevicesToUser(deviceID, username);
    }

    @Override
    public void unLinkUser(String username, UUID deviceID) {
        throwIfNull(deviceID, "device_Id cannot be null");
        throwIfBlank(username, "username cannot be null or empty");
        throwIFDeviceNotExists(deviceID);
        throwIFUserNotExists(username);
        linkRepo.unLinkDevicesToUser(deviceID, username);
    }

    private void throwIFUserNotExists(String username) {
        if(!userRepo.isExists(username)) {
            throw new IllegalException(USER_NOT_EXISTS, format("The username '%s' does not exist", username));
        }
    }
    private void throwIFDeviceNotExists(UUID deviceId) {
        if(!deviceRepo.isExists(deviceId.toString())) {
            throw new IllegalException(DEVICE_NOT_EXISTS, format("The device_id '%s' does not exist", deviceId));
        }
    }

    private void throwIFDeviceExists(UUID deviceId) {
        if(deviceRepo.isExists(deviceId.toString())) {
            throw new IllegalException(DEVICE_ALREADY_EXISTS, format("The device_id '%s' already exist", deviceId));
        }
    }

    private void throwIfBlank(String toCheck, String message) {
        if(StringUtils.isBlank(toCheck)) {
            throw new IllegalException(WRONG_VALIDATION, message);
        }
    }

    private void throwIfNull(Object toCheck, String message) {
        if(toCheck == null) {
            throw new IllegalException(WRONG_VALIDATION, message);
        }
    }

    private void throwIfUserListNotExists(List<String> userNames, UUID deviceId) {
        userNames.forEach(username -> {
            if(!userRepo.isExists(username)) {
                throw new IllegalException(
                        USER_NOT_EXISTS,
                        format("The username '%s' linked to the device_Id '%s' does not exist", username, deviceId));
            }
        });
    }
}
