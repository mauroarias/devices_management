package org.etcsoft.devicemanagement.server;

import lombok.NonNull;
import org.etcsoft.devicemanagement.model.Device;
import org.etcsoft.devicemanagement.services.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Device controller server
 */
@RestController
@RequestMapping(value = "deviceManagement/devices")
public final class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService)
    {
        this.deviceService = deviceService;
    }

    @RequestMapping(method = POST)
    ResponseEntity createDevice(@RequestBody() @NonNull @Valid Device device) {
        return new ResponseEntity(deviceService.create(device), OK);
    }

    @RequestMapping(value = "/{deviceID}", method = PUT)
    ResponseEntity updateDevice(@RequestBody() @NonNull @Valid Device device,
                                @PathVariable("deviceID") @NonNull String deviceID) {
        deviceService.update(device, UUID.fromString(deviceID));
        return new ResponseEntity(OK);
    }

    @RequestMapping(value = "/{deviceID}", method = GET)
    ResponseEntity getDevice(@PathVariable("deviceID") @NonNull String deviceID) {
        return new ResponseEntity(deviceService.get(UUID.fromString(deviceID)), OK);
    }

    @RequestMapping(value = "/{deviceID}", method = DELETE)
    ResponseEntity deleteDevice(@PathVariable("deviceID") @NonNull String deviceID) {
        deviceService.delete(UUID.fromString(deviceID));
        return new ResponseEntity(OK);
    }

    @RequestMapping(value = "/{deviceID}/user/{username}", method = PUT)
    ResponseEntity deviceLinkUser(@PathVariable("deviceID") @NonNull String deviceID,
                                  @PathVariable("username") @NonNull String username) {
        deviceService.linkUser(username, UUID.fromString(deviceID));
        return new ResponseEntity(OK);
    }

    @RequestMapping(value = "/{deviceID}/user/{username}", method = DELETE)
    ResponseEntity deviceUnlinkUser(@PathVariable("deviceID") @NonNull String deviceID,
                                            @PathVariable("username") @NonNull String username) {
        deviceService.unLinkUser(username, UUID.fromString(deviceID));
        return new ResponseEntity(OK);
    }
}
