package org.etcsoft.devicemanagement.repository;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;

/**
 * Created by mauro on 22/04/16.
 */
public class AbstractMysql implements Closeable {
    private MysqlConnection mysqlConnection;

    AbstractMysql(MysqlConnection jdbcConnection){
        this.mysqlConnection = jdbcConnection;
    }

    Connection getConnector(){
        return mysqlConnection.getConnection();
    }

    @Override
    public void close() throws IOException {
        mysqlConnection.close();
    }
}
