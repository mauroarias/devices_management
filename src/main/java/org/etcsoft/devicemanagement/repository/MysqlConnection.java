package org.etcsoft.devicemanagement.repository;

import lombok.SneakyThrows;
import lombok.Value;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import static java.lang.String.format;

/**
 * Created by mauro on 22/04/16.
 */
@Value
public class MysqlConnection implements Closeable{
    private final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

    private final Connection connection;

    @SneakyThrows
    MysqlConnection(String hostname, int port, String path, String user, String password) {

        Class.forName(MYSQL_DRIVER);

        String mysqlUrl =
                format("jdbc:mysql://%s:%d/%s", hostname, port, path);

        connection = DriverManager.getConnection(mysqlUrl, user, password);
    }

    @Override
    @SneakyThrows
    public void close() throws IOException {
        connection.close();
    }
}
