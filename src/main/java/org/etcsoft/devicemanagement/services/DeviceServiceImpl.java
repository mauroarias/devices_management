package org.etcsoft.devicemanagement.services;

import org.etcsoft.devicemanagement.model.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by mauro on 04/04/16.
 */
@Service
public class DeviceServiceImpl implements DeviceService {

    public DeviceServiceImpl() { }

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
