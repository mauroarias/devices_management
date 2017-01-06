package org.etcsoft.devicemanagement.services;

import org.apache.commons.lang3.StringUtils;
import org.etcsoft.devicemanagement.model.Exceptions.IllegalException;
import org.etcsoft.devicemanagement.model.User;
import org.etcsoft.devicemanagement.repository.DeviceRepo;
import org.etcsoft.devicemanagement.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static org.etcsoft.devicemanagement.model.Enums.ErrorCodes.*;

@Service
public final class DefaultUserService implements UserService {

    private final UserRepo userRepo;
    private final DeviceRepo deviceRepo;

    @Autowired
    public DefaultUserService(UserRepo userRepo, DeviceRepo deviceRepo) {
        this.userRepo = userRepo;
        this.deviceRepo = deviceRepo;
    }

    @Override
    public User create(User user) {
        throwIfNull(user, "User body cannot be null");
        throwIFUserExists(user.getUsername());
        throwIfBlank(user.getPasswd(), "during creation, password cannot be null or empty");
        throwIfDeviceNotExists(user.getDevices(), user.getUsername());
        userRepo.insert(user);
        return User
                .builder()
                .devices(user.getDevices())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .build();
    }

    @Override
    public User get(String userName) {
        throwIfBlank(userName, "username cannot be null or empty");
        throwIFUserNotExists(userName);
        Optional<User> user = userRepo.select(userName);
        if(!user.isPresent()) {
            throw new IllegalException(
                    UNKNOWN_ERROR,
                    format("User %s cannot be read from Database", userName));
        }
        return user.get();
    }

    @Override
    public void update(User user, String username) {
        throwIfNull(user, "User body cannot be null");
        throwIfBlank(username, "username cannot be null or empty");
        throwIFUserNotExists(username);
        userRepo.update(user, username);
    }

    @Override
    public void updatePassword(String username, String password) {
        throwIFUserNotExists(username);
        throwIfBlank(username, "username cannot be null or empty");
        throwIfBlank(password, "password cannot be null or empty");
        userRepo.updatePassword(username, password);
    }

    @Override
    public void delete(String username) {
        throwIfBlank(username, "username cannot be null or empty");
        throwIFUserNotExists(username);
        userRepo.drop(username);
    }

    private void throwIFUserNotExists(String username) {
        if(!userRepo.isExists(username)) {
            throw new IllegalException(USER_NOT_EXISTS, format("The username '%s' does not exist", username));
        }
    }

    private void throwIFUserExists(String username) {
        if(userRepo.isExists(username)) {
            throw new IllegalException(USER_ALREADY_EXISTS, format("The username '%s' already exist", username));
        }
    }

    private void throwIfBlank(String toCheck, String message) {
        if(StringUtils.isBlank(toCheck)) {
            throw new IllegalException(WRONG_VALIDATION, message);
        }
    }

    private void throwIfNull(User user, String message) {
        if(user == null) {
            throw new IllegalException(WRONG_VALIDATION, message);
        }
    }

    private void throwIfDeviceNotExists(List<UUID> deviceIds, String username) {
        deviceIds.forEach(device -> {
            if(!deviceRepo.isExists(device.toString())) {
                throw new IllegalException(
                        DEVICE_NOT_EXISTS,
                        format("The device_Id '%s' linked to username '%s' does not exist", device, username));
            }
        });
    }
}
