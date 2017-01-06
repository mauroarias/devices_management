package org.etcsoft.devicemanagement.repository;

import org.etcsoft.devicemanagement.model.Device;

import java.util.Optional;
import java.util.UUID;

public interface DeviceRepo {
    void insert(Device device);
    void update(Device device, UUID deviceId);
    void drop(String deviceId);
    Optional<Device> select(String deviceId);
    boolean isExists(String deviceId);
}
