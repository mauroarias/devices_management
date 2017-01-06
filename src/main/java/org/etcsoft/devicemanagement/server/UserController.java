package org.etcsoft.devicemanagement.server;


import lombok.NonNull;
import org.etcsoft.devicemanagement.model.User;
import org.etcsoft.devicemanagement.services.DeviceService;
import org.etcsoft.devicemanagement.services.UserService;
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
@RequestMapping(value = "deviceManagement/users")
public class UserController {

    private final UserService userService;
    private final DeviceService deviceService;

    @Autowired
    public UserController(UserService userService,
                          DeviceService deviceService)
    {
        this.userService = userService;
        this.deviceService = deviceService;
    }

    @RequestMapping(method = POST)
    ResponseEntity createUser(@RequestBody() @NonNull @Valid User user) {
        return new ResponseEntity(userService.create(user), OK);
    }

    @RequestMapping(value = "/{username}", method = PUT)
    ResponseEntity updateUser(@RequestBody() @NonNull @Valid User user,
                              @PathVariable("username") @NonNull String username) {
        userService.update(user, username);
        return new ResponseEntity(OK);
    }

    @RequestMapping(value = "/password/{username}", method = PUT)
    ResponseEntity updatePassword(@RequestBody() @NonNull @Valid User user,
                                  @PathVariable("username") @NonNull String username) {
        userService.updatePassword(username, user.getPasswd());
        return new ResponseEntity(OK);
    }

    @RequestMapping(value = "/{username}", method = GET)
    ResponseEntity getuser(@PathVariable("username") @NonNull String username) {
        return new ResponseEntity(userService.get(username), OK);
    }

    @RequestMapping(value = "/{username}", method = DELETE)
    ResponseEntity deleteUser(@PathVariable("deviceID") @NonNull String username) {
        userService.delete(username);
        return new ResponseEntity(OK);
    }

    @RequestMapping(value = "/{username}/device/{deviceId}", method = PUT)
    ResponseEntity deviceLinkUser(@PathVariable("deviceId") @NonNull String deviceId,
                                  @PathVariable("username") @NonNull String username) {
        deviceService.linkUser(username, UUID.fromString(deviceId));
        return new ResponseEntity(OK);
    }

    @RequestMapping(value = "/{username}/device/{deviceId}", method = DELETE)
    ResponseEntity deviceUnlinkUser(@PathVariable("deviceId") @NonNull String deviceId,
                                    @PathVariable("username") @NonNull String username) {
        deviceService.unLinkUser(username, UUID.fromString(deviceId));
        return new ResponseEntity(OK);
    }
}
