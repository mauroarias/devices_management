package org.etcsoft.devicemanagement.repository;

import com.mysql.jdbc.Connection;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.etcsoft.devicemanagement.model.Device;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MysqlDeviceRepoTest {
    private HikariDataSource hikariDataSource = mock(HikariDataSource.class);
    private Connection connection = mock(Connection.class);
    private PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private DeviceRepo deviceRepo = new MysqlDeviceRepo(hikariDataSource);
    private final Device defaultDevice = Device
            .builder()
            .owner("username")
            .property("myProperty", 55)
            .partNumber("partNumber")
            .deviceId(UUID.randomUUID())
            .fwVersion("V1.0.1")
            .lasUpdate(DateTime.now())
            .build();
    @Before
    @SneakyThrows
    public void setup() {
        when(hikariDataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
    }

    @Test
    public void whenInsertDevice_ThenOk() {
        //checking for not exception
        deviceRepo.insert(defaultDevice);
    }

    @Test
    public void whenUpdateDevice_ThenOk() {
        //checking for not exception
        deviceRepo.update(defaultDevice, UUID.randomUUID());
    }

    @Test
    public void whenDropDevice_ThenOk() {
        //checking for not exception
        deviceRepo.drop(UUID.randomUUID().toString());
    }

    @Test
    @SneakyThrows
    public void whenIsExistsDevice_ThenOk() {
        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        Assert.assertTrue(deviceRepo.isExists("myuser"));
    }

    @Test
    @SneakyThrows
    public void whenIsNotExistsDevice_ThenOk() {
        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        Assert.assertFalse(deviceRepo.isExists("myuser"));
    }

    @Test
    @SneakyThrows
    public void whenSelectDevice_ThenOk() {
        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(eq("property_name"))).thenReturn("myProperty");
        when(resultSet.getObject(eq("value"))).thenReturn(55);
        when(resultSet.getString(eq("part_number"))).thenReturn(defaultDevice.getPartNumber());
        when(resultSet.getString(eq("fw_version"))).thenReturn(defaultDevice.getFwVersion());
        when(resultSet.getTimestamp("last_update")).thenReturn(new Timestamp(defaultDevice.getLasUpdate().getMillis()));
        when(resultSet.getString(eq("username"))).thenReturn(defaultDevice.getOwners().get(0));
        Optional<Device> device = deviceRepo.select(defaultDevice.getDeviceId().toString());
        Assert.assertTrue(device.isPresent());
        Assert.assertEquals(defaultDevice, device.get());
    }
}
