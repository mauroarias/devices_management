package org.etcsoft.devicemanagement.repository;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

/**
 * Created by mauro on 04/05/16.
 */
public class DevicesByUserImpl {

    private final static String COLUMN_DEVICE_NAME = "device_name";
    private final static String COLUMN_USER = "user";
    private final static String TABLE_NAME = "device_by_user";
    private final static String INSERT_QUERY =
            format("INSERT INTO %S (%S, %S) VALUES (?, ?)",
                    TABLE_NAME,
                    COLUMN_DEVICE_NAME,
                    COLUMN_USER);
    private final static String DELETE_BY_USER_QUERY =
            format("DELETE from %s WHERE %s = ?",
                    TABLE_NAME,
                    COLUMN_USER);
    private final static String DELETE_BY_DEVICE_QUERY =
            format("DELETE from %s WHERE %s = ?",
                    TABLE_NAME,
                    COLUMN_DEVICE_NAME);
    private final static String SELECT_BY_USER_QUERY =
            format("SELECT %s FROM %s Where %s = ?",
                    COLUMN_DEVICE_NAME,
                    TABLE_NAME,
                    COLUMN_USER);
    private final static String SELECT_BY_DEVICE_QUERY =
            format("SELECT %s FROM %s Where %s = ?",
                    COLUMN_USER,
                    TABLE_NAME,
                    COLUMN_DEVICE_NAME);


    private final Logger logger = Logger.getLogger(DevicesByUserImpl.class);

    public List<String> getDevices(Connection connector, String user) throws SQLException {

        PreparedStatement selectStatement = null;

        try {

            selectStatement = connector.prepareStatement(SELECT_BY_USER_QUERY);
            selectStatement.setString(1, user);

            logger.debug(format("sending select query %s", selectStatement.toString()));

            ResultSet result = selectStatement.executeQuery();

            List<String> users = new ArrayList<>();

            while (result.next()) {
                users.add(result.getString(COLUMN_DEVICE_NAME));
            }
            return users;

        } finally {

            if (selectStatement != null) {

                selectStatement.close();
            }
        }
    }

    public List<String> getUsers(Connection connector, String deviceName) throws SQLException {

        PreparedStatement selectStatement = null;

        try {

            selectStatement = connector.prepareStatement(SELECT_BY_DEVICE_QUERY);
            selectStatement.setString(1, deviceName);

            logger.debug(format("sending select query %s", selectStatement.toString()));

            ResultSet result = selectStatement.executeQuery();

            List<String> devices = new ArrayList<>();

            while (result.next()) {
                devices.add(result.getString(COLUMN_USER));
            }
            return devices;

        } finally {

            if (selectStatement != null) {

                selectStatement.close();
            }
        }
    }

    public void deleteByUser(Connection connector, String user) throws SQLException {

        PreparedStatement deleteStatement = null;

        try {

            deleteStatement = connector.prepareStatement(DELETE_BY_USER_QUERY);
            deleteStatement.setString(1, user);

            logger.debug(format("sending delete query %s", deleteStatement.toString()));

            deleteStatement.execute();

        } finally {

            if (deleteStatement != null) {

                deleteStatement.close();
            }
        }
    }

    public void deleteByDevice(Connection connector, String deviceName) throws SQLException {

        PreparedStatement deleteStatement = null;

        try {

            deleteStatement = connector.prepareStatement(DELETE_BY_DEVICE_QUERY);
            deleteStatement.setString(1, deviceName);

            logger.debug(format("sending delete query %s", deleteStatement.toString()));

            deleteStatement.execute();

        } finally {

            if (deleteStatement != null) {

                deleteStatement.close();
            }
        }
    }

    public void insert(Connection connector, String user, String deviceName) throws SQLException {

        PreparedStatement insertStatement = null;

        try {

            insertStatement = connector.prepareStatement(INSERT_QUERY);

            insertStatement.setString(1, deviceName);
            insertStatement.setString(2, user);

            logger.debug(format("sending insert query %s", insertStatement.toString()));

            insertStatement.execute();

        } finally {

            if (insertStatement != null) {

                insertStatement.close();
            }
        }
    }
}
