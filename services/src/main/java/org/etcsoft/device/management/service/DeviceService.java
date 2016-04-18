package org.etcsoft.device.management.service;

import org.etcsoft.device.management.model.Device;

import java.util.UUID;

/**
 * Created by mauro on 04/04/16.
 */
public interface DeviceService {

    Device addDevice(Device device);
    void updateDevice(Device device, String DeviceName);
    void updateDevice(Device device, UUID DeviceID);
    void deleteDevice(String DeviceName);
    void deleteDevice(UUID DeviceID);
}
