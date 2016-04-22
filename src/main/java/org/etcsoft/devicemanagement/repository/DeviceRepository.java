package org.etcsoft.devicemanagement.repository;

import org.etcsoft.devicemanagement.model.Device;

/**
 * Created by mauro on 22/04/16.
 */
public interface DeviceRepository {

    void insert(Device device);
    void update(Device device, String deviceId);
    void drop(String deviceId);
    void select(String deviceId);
}
