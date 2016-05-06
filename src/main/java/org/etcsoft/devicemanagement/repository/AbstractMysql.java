package org.etcsoft.devicemanagement.repository;

import lombok.Data;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;

import static java.lang.String.format;

/**
 * Created by mauro on 22/04/16.
 */
@Data
public abstract class AbstractMysql implements Closeable {

    private final static String MYSQL_DRIVER_NAME = "com.mysql.jdbc.Driver";

    private final Logger logger = Logger.getLogger(AbstractMysql.class);
    private final Connection connector;
    private final boolean isAutocommit;
    private final PropertyrepoMysql propertyRepo = new PropertyrepoMysql();
    private final DevicesByUserImpl devicesByUserRepo = new DevicesByUserImpl();

    AbstractMysql(DatabaseConfig mysqlConfig) throws Exception {

        isAutocommit = mysqlConfig.isAutoCommit();

        Class.forName(MYSQL_DRIVER_NAME);

        String urlDriver =
                format("jdbc:mysql://%s:%d/%s?connectTimeout=%d&socketTimeout=%d",
                        mysqlConfig.getHostname(),
                        mysqlConfig.getPort(),
                        mysqlConfig.getDatabaseName(),
                        mysqlConfig.getConnectionTimeout(),
                        mysqlConfig.getConnectionTimeout());

        logger.debug(format("connecting to Mysql using driver: %s and URL: %s", MYSQL_DRIVER_NAME, urlDriver));

        connector = DriverManager.getConnection(urlDriver, mysqlConfig.getUser(), mysqlConfig.getPassword());

        connector.setAutoCommit(mysqlConfig.isAutoCommit());
    }

    @Override
    @SneakyThrows
    public void close() throws IOException {
        logger.debug("disconnecting Mysql");

        connector.close();
    }

    protected void statementCommit() throws SQLException {

        if(!isAutocommit) {

            connector.commit();
        }
    }

    protected void closeTransaction(Statement statement) {
        try {
            if (statement != null) {

                statement.close();
            }
        } catch(SQLException ex) {

            throw new IllegalStateException(ex.getMessage());
        }
    }

    protected void rollbackTransaction() {

        try {
            getConnector().rollback();
        } catch (SQLException ex) {

            throw new IllegalStateException(ex.getMessage());
        }
    }
}
