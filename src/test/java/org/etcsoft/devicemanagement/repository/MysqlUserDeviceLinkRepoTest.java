package org.etcsoft.devicemanagement.repository;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MysqlUserDeviceLinkRepoTest {
    private HikariDataSource hikariDataSource = mock(HikariDataSource.class);
    private Connection connection = mock(Connection.class);
    private PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private UserDeviceLinkRepo userDeviceLinkRepo = new MysqlUserDeviceLinkRepo(hikariDataSource);

    @Before
    @SneakyThrows
    public void setup() {
        when(hikariDataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
    }

    @Test
    public void whenLink_ThenOk() {
        //checking for not exception
        userDeviceLinkRepo.linkDevicesToUser(UUID.randomUUID(), "username");
    }

    @Test
    public void whenUnlink_ThenOk() {
        //checking for not exception
        userDeviceLinkRepo.unLinkDevicesToUser(UUID.randomUUID(), "username");
    }
}
