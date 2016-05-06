package org.etcsoft.devicemanagement.repository;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Created by mauro on 30/04/16.
 */
public class PropertyrepoMysql {
    public enum ResourceTypes {
        DEVICE,
        USER
    }

    private final static String COLUMN_RESOURCE_NAME = "resource_name";
    private final static String COLUMN_RESOURCE_TYPE = "resource_type";
    private final static String COLUMN_NAME = "name";
    private final static String COLUMN_VALUE = "value";
    private final static String TABLE_NAME = "properties";
    private final static String INSERT_QUERY =
            format("INSERT INTO %S (%S, %S, %S, %S) VALUES (?, ?, ?, ?)",
                    TABLE_NAME,
                    COLUMN_RESOURCE_TYPE,
                    COLUMN_RESOURCE_NAME,
                    COLUMN_NAME,
                    COLUMN_VALUE);
    private final static String DELETE_ALL_QUERY =
            format("DELETE from %s WHERE %s = ? AND %s = ?",
                    TABLE_NAME,
                    COLUMN_RESOURCE_NAME,
                    COLUMN_RESOURCE_TYPE);
    private final static String SELECT_ALL_QUERY =
            format("SELECT * FROM %s Where %s = ? AND %s = ?",
                    TABLE_NAME,
                    COLUMN_RESOURCE_NAME,
                    COLUMN_RESOURCE_TYPE);

    private final static String SELECT_ONE_QUERY =
            format("SELECT * FROM %s Where %s = ? AND %s = ? AND %s = ?",
                    TABLE_NAME,
                    COLUMN_RESOURCE_NAME,
                    COLUMN_RESOURCE_TYPE,
                    COLUMN_NAME);

    private final Logger logger = Logger.getLogger(PropertyrepoMysql.class);

    public Map<String, Object> getProperties(Connection connector,
                                             String resourceName,
                                             ResourceTypes resourceType) throws SQLException {

        PreparedStatement selectStatement = null;

        try {

            selectStatement = connector.prepareStatement(SELECT_ALL_QUERY);
            selectStatement.setString(1, resourceName);
            selectStatement.setString(2, resourceType.toString().toLowerCase());

            logger.debug(format("sending select query %s", selectStatement.toString()));

            ResultSet result = selectStatement.executeQuery();

            Map<String, Object> properties = new HashMap<>();

            while (result.next()) {
                properties.put(
                        result.getString(COLUMN_NAME),
                        result.getObject(COLUMN_VALUE));
            }
            return properties;

        } finally {

            if (selectStatement != null) {

                selectStatement.close();
            }
        }
    }

    public Optional<Object> getProperty(Connection connector,
                                        String resourceName,
                                        String resourceType,
                                        String propertyName) throws SQLException {
        PreparedStatement selectStatement = null;

        try {

            selectStatement = connector.prepareStatement(SELECT_ONE_QUERY);
            selectStatement.setString(1, resourceName);
            selectStatement.setString(2, resourceType);
            selectStatement.setString(3, propertyName);

            logger.debug(format("sending select query %s", selectStatement.toString()));

            ResultSet result = selectStatement.executeQuery();

            return Optional.ofNullable(
                    result.next() ?
                            result.getObject(COLUMN_VALUE) :
                            null);

        } finally {

            if (selectStatement != null) {

                selectStatement.close();
            }
        }
    }

    public void deleteProperties(String resourceName,
                                 ResourceTypes resourceType,
                                 Connection connector) throws SQLException {

        PreparedStatement deleteStatement = null;

        try {

            deleteStatement = connector.prepareStatement(DELETE_ALL_QUERY);
            deleteStatement.setString(1, resourceName);
            deleteStatement.setString(2, resourceType.toString().toLowerCase());

            logger.debug(format("sending delete query %s", deleteStatement.toString()));

            deleteStatement.execute();

        } finally {

            if (deleteStatement != null) {

                deleteStatement.close();
            }
        }
    }

    public void insert(Map<String, Object> properties,
                       String resourceName,
                       ResourceTypes resourceType,
                       Connection connector) throws SQLException {

        PreparedStatement insertStatement = null;

        try {

            insertStatement = connector.prepareStatement(INSERT_QUERY);

            for (Map.Entry<String, Object> property : properties.entrySet()) {
                insertStatement.setString(1, resourceType.toString().toLowerCase());
                insertStatement.setString(2, resourceName);
                insertStatement.setString(3, property.getKey());
                insertStatement.setObject(4, property.getValue());
                insertStatement.addBatch();
            }
            logger.debug(format("sending insert query %s", insertStatement.toString()));

            insertStatement.executeBatch();

        } finally {

            if (insertStatement != null) {

                insertStatement.close();
            }
        }
    }
}
