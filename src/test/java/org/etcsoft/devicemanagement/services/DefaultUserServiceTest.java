package org.etcsoft.devicemanagement.services;

import org.etcsoft.devicemanagement.model.Exceptions.IllegalException;
import org.etcsoft.devicemanagement.model.User;
import org.etcsoft.devicemanagement.repository.DeviceRepo;
import org.etcsoft.devicemanagement.repository.UserRepo;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.UUID;

import static java.lang.String.format;
import static java.util.Optional.of;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class DefaultUserServiceTest {
    private final UserRepo userRepo = mock(UserRepo.class);
    private final DeviceRepo deviceRepo = mock(DeviceRepo.class);
    private final UserService userService = new DefaultUserService(userRepo, deviceRepo);
    private final UUID deviceId = UUID.randomUUID();
    private final User defaultUser = User
            .builder()
            .email("user@yahoo.com")
            .username("user")
            .firstName("myusername")
            .lastName("mylastname")
            .device(deviceId)
            .build();
    private final User defaultUserWithPassword = User
            .builder()
            .email(defaultUser.getEmail())
            .username(defaultUser.getUsername())
            .firstName(defaultUser.getFirstName())
            .lastName(defaultUser.getLastName())
            .passwd("mypassword")
            .device(deviceId)
            .build();

    @Rule
    public final ExpectedException thrownExpected = ExpectedException.none();

    @Test
    public void whenCreateUser_ThenOk() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        when(userRepo.isExists(any(String.class))).thenReturn(false);
        User userCreated = userService.create(defaultUserWithPassword);
        Assert.assertEquals(defaultUser, userCreated);
    }

    @Test
    public void whenUpdateUser_ThenOk() {
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        userService.update(defaultUser, "username");
    }

    @Test
    public void whenDeleteUser_ThenOk() {
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        userService.delete("username");
    }

    @Test
    public void whenGetUser_ThenOk() {
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        when(userRepo.select(any(String.class))).thenReturn(of(defaultUser));
        User userRead = userService.get("username");
        Assert.assertSame(defaultUser, userRead);
    }

    @Test
    public void whenUpdatePassword_ThenOk() {
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        userService.updatePassword("username", "password");
    }

    @Test
    public void whenCreateUserWithUserNull_ThenException() {
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("User body cannot be null");
        userService.create(null);
    }

    @Test
    public void whenCreateUserWithExistingUser_ThenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("The username 'user' already exist");
        userService.create(defaultUser);
    }

    @Test
    public void whenCreateUserWithNotExistingDeviceId_ThenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(false);
        when(userRepo.isExists(any(String.class))).thenReturn(false);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage(format("The device_Id '%s' linked to username 'user' does not exist", deviceId));
        userService.create(defaultUserWithPassword);
    }

    @Test
    public void whenCreateUserWithNotPassword_ThenException() {
        when(deviceRepo.isExists(any(String.class))).thenReturn(true);
        when(userRepo.isExists(any(String.class))).thenReturn(false);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("during creation, password cannot be null or empty");
        userService.create(defaultUser);
    }

    @Test
    public void whenUpdateUserWithNotExistingUser_ThenException() {
        when(userRepo.isExists(any(String.class))).thenReturn(false);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("The username 'username' does not exist");
        userService.update(defaultUser, "username");
    }

    @Test
    public void whenUpdateUserWithUserNull_ThenException() {
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("User body cannot be null");
        userService.update(null, "username");
    }

    @Test
    public void whenUpdateUserWithUsernameNull_ThenException() {
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("username cannot be null or empty");
        userService.update(defaultUser, null);
    }

    @Test
    public void whenUpdateUserWithUsernameBlank_ThenException() {
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("username cannot be null or empty");
        userService.update(defaultUser, "");
    }

    @Test
    public void whenDeleteUserWithUsernameBlank_ThenException() {
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("username cannot be null or empty");
        userService.delete("");
    }

    @Test
    public void whenDeleteUserWithUsernameNull_ThenException() {
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("username cannot be null or empty");
        userService.delete(null);
    }

    @Test
    public void whenDeleteUserWithNotExists_ThenException() {
        when(userRepo.isExists(any(String.class))).thenReturn(false);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("The username 'username' does not exist");
        userService.delete("username");
    }

    @Test
    public void whenUpdatePasswordWithUsernameNull_ThenException() {
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("username cannot be null or empty");
        userService.updatePassword(null, "password");
    }

    @Test
    public void whenUpdatePasswordWithUsernameEmpty_ThenException() {
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("username cannot be null or empty");
        userService.updatePassword("", "password");
    }

    @Test
    public void whenUpdatePasswordWithPasswordNull_ThenException() {
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("password cannot be null or empty");
        userService.updatePassword("username", null);
    }

    @Test
    public void whenUpdatePasswordWithPasswordEmpty_ThenException() {
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("password cannot be null or empty");
        userService.updatePassword("username", "");
    }

    @Test
    public void whenUpdatePasswordWithUsernameNotExists_ThenException() {
        when(userRepo.isExists(any(String.class))).thenReturn(false);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("The username 'username' does not exist");
        userService.updatePassword("username", "password");
    }

    @Test
    public void whenGetUserWithUsernameNull_ThenException() {
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("username cannot be null or empty");
        userService.get(null);
    }

    @Test
    public void whenGetUserWithUsernameEmpty_ThenException() {
        when(userRepo.isExists(any(String.class))).thenReturn(true);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("username cannot be null or empty");
        userService.get("");
    }

    @Test
    public void whenGetUserWithNotExists_ThenException() {
        when(userRepo.isExists(any(String.class))).thenReturn(false);
        thrownExpected.expect(IllegalException.class);
        thrownExpected.expectMessage("The username 'username' does not exist");
        userService.get("username");
    }
}
