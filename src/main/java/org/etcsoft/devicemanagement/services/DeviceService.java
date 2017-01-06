package org.etcsoft.devicemanagement.services;

import org.etcsoft.devicemanagement.model.Device;

import java.util.UUID;

public interface DeviceService {
    Device create(Device device);
    Device get(UUID device);
    void update(Device device, UUID deviceID);
    void delete(UUID deviceID);
    void linkUser(String username, UUID deviceID);
    void unLinkUser(String username, UUID deviceID);
}
