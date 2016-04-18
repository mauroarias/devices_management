package org.etcsoft.device.management.service;

import org.etcsoft.device.management.model.Device;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by mauro on 04/04/16.
 */
@Service
public class DeviceServiceImpl implements DeviceService {


    @Override
    public Device addDevice(Device device) {
        return null;
    }

    @Override
    public void updateDevice(Device device, String DeviceName) {

    }

    @Override
    public void updateDevice(Device device, UUID DeviceID) {

    }

    @Override
    public void deleteDevice(String DeviceName) {

    }

    @Override
    public void deleteDevice(UUID DeviceID) {

    }
}
