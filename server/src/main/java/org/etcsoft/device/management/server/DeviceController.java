package org.etcsoft.device.management.server;

import org.etcsoft.device.management.model.Device;
import org.etcsoft.device.management.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

/**
 * Created by mauro on 27/03/16.
 */
@RestController
@RequestMapping(value = "devices")
public class DeviceController {

    @Autowired
    DeviceService deviceService;

    @RequestMapping(value = "/{deviceName}", method = DELETE)
    public void deleteDevice(@PathVariable("deviceName") String deviceName) {
        deviceService.deleteDevice(deviceName);
    }
}
