package org.etcsoft.devicemanagement.server;

import org.etcsoft.devicemanagement.services.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

/**
 * Created by mauro on 27/03/16.
 */
@RestController
@RequestMapping(value = "devices")
public final class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService)
    {
        this.deviceService = deviceService;
    }

    @RequestMapping(value = "/{deviceName}", method = DELETE)
    public void deleteDevice(@PathVariable("deviceName") String deviceName) {
        deviceService.deleteDevice(deviceName);
    }
}
