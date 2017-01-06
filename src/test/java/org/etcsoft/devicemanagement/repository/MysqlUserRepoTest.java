package org.etcsoft.devicemanagement.repository;

import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.etcsoft.devicemanagement.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MysqlUserRepoTest {
    private HikariDataSource hikariDataSource = mock(HikariDataSource.class);
    private Connection connection = mock(Connection.class);
    private PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private UserRepo userRepo = new MysqlUserRepo(hikariDataSource);
    private final User defaultUser = User
            .builder()
            .email("user@yahoo.com")
            .username("user")
            .firstName("myusername")
            .lastName("mylastname")
            .device(UUID.randomUUID())
            .build();

    @Before
    @SneakyThrows
    public void setup() {
        when(hikariDataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
    }

    @Test
    public void whenInsertUser_ThenOk() {
        //checking for not exception
        userRepo.insert(defaultUser);
    }

    @Test
    public void whenUpdateUser_ThenOk() {
        //checking for not exception
        userRepo.update(defaultUser, "myuser");
    }

    @Test
    public void whenDropUser_ThenOk() {
        //checking for not exception
        userRepo.drop("myuser");
    }

    @Test
    @SneakyThrows
    public void whenIsExistsUser_ThenOk() {
        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        Assert.assertTrue(userRepo.isExists("myuser"));
    }

    @Test
    @SneakyThrows
    public void whenIsNotExistsUser_ThenOk() {
        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        Assert.assertFalse(userRepo.isExists("myuser"));
    }

    @Test
    public void whenUpdatePassword_ThenOk() {
        //checking for not exception
        userRepo.updatePassword("myuser", "mynewPassword");
    }

    @Test
    @SneakyThrows
    public void whenSelectUser_ThenOk() {
        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(eq("email"))).thenReturn(defaultUser.getEmail());
        when(resultSet.getString(eq("first_name"))).thenReturn(defaultUser.getFirstName());
        when(resultSet.getString(eq("last_name"))).thenReturn(defaultUser.getLastName());
        when(resultSet.getString(eq("device_id"))).thenReturn(defaultUser.getDevices().get(0).toString());
        Optional<User> user = userRepo.select("user");
        Assert.assertTrue(user.isPresent());
        Assert.assertEquals(defaultUser, user.get());
    }
}