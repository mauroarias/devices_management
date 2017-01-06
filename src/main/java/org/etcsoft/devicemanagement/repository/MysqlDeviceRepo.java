package org.etcsoft.devicemanagement.repository;

import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.etcsoft.devicemanagement.model.Device;
import org.etcsoft.devicemanagement.model.Exceptions.IllegalException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.etcsoft.devicemanagement.model.Enums.ErrorCodes.DATABASE_ACCESS;
import static org.etcsoft.devicemanagement.repository.Constants.NAMESPACE;

@Component
public final class MysqlDeviceRepo implements DeviceRepo {
    private final static Logger logger = Logger.getLogger(MysqlDeviceRepo.class);

    private final HikariDataSource dataSource;

    @Autowired
    public MysqlDeviceRepo(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @SneakyThrows
    public void insert(Device device) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                logger.debug(format("Performing an insert device id: %s", device.getDeviceId().toString()));

                final PreparedStatement insertDevice = connection.prepareStatement(
                        "INSERT INTO " + NAMESPACE + ".device " +
                                "(device_id, fw_version, last_update, manufacture, part_number) " +
                                "VALUES (?, ?, ?, ?, ?)");
                insertDevice.setString(1, device.getDeviceId().toString());
                insertDevice.setString(2, device.getFwVersion());
                insertDevice.setTimestamp(3,
                        device.getLasUpdate() != null ?
                                new Timestamp(device.getLasUpdate().getMillis()) :
                                null);
                insertDevice.setString(4, device.getManufacture());
                insertDevice.setString(5, device.getPartNumber());
                insertDevice.execute();

                if(device.getProperties() != null) {
                    final PreparedStatement insertProperties = connection.prepareStatement(
                            "INSERT INTO " + NAMESPACE + ".properties (device_id, property_name, value) VALUES (?, ?, ?)");
                    for (Map.Entry<String, Object> property : device.getProperties().entrySet()) {
                        insertProperties.setString(1, device.getDeviceId().toString());
                        insertProperties.setString(2, property.getKey());
                        insertProperties.setObject(3, property.getValue());
                        insertProperties.addBatch();
                    }
                    insertProperties.executeBatch();
                }

                if(device.getOwners() != null) {
                    final PreparedStatement insertOwnerLinks = connection.prepareStatement(
                            "INSERT INTO " + NAMESPACE + ".owner_device_relationship (device_id, username) VALUES (?, ?)");
                    for (String owner : device.getOwners()) {
                        insertOwnerLinks.setString(1, device.getDeviceId().toString());
                        insertOwnerLinks.setString(2, owner);
                        insertOwnerLinks.addBatch();
                    }
                    insertOwnerLinks.executeBatch();
                }
                connection.commit();

            } catch (Exception ex) {

                logger.debug("something was wrong during insert, rollback will be performed");
                MysqlUtils.rollbackTransaction("insert", connection);
                throw new IllegalException(DATABASE_ACCESS, format(
                        "Something was wrong during insert, error %s", ex.getMessage()));
            }
        }
    }

    @Override
    @SneakyThrows
    public void update(Device device, UUID deviceId) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                logger.debug(format("Performing an update device id: %s", deviceId.toString()));

                final PreparedStatement updateDevice = connection.prepareStatement(
                        "UPDATE " + NAMESPACE + ".device " +
                        "SET fw_version = COALESCE(?, fw_version), " +
                            "last_update = COALESCE(?, last_update), " +
                            "manufacture = COALESCE(?, manufacture), " +
                            "part_number = COALESCE(?, part_number) " +
                        "WHERE device_id = ?");
                updateDevice.setString(1, device.getFwVersion());
                updateDevice.setTimestamp(2,
                        device.getLasUpdate() != null ?
                                new Timestamp(device.getLasUpdate().getMillis()) :
                                null);
                updateDevice.setString(3, device.getManufacture());
                updateDevice.setString(4, device.getPartNumber());
                updateDevice.setString(5, deviceId.toString());
                updateDevice.execute();

                if(device.getProperties() != null) {
                    final PreparedStatement insertUpdateProperties = connection.prepareStatement(
                            "INSERT INTO " + NAMESPACE + ".properties (device_id, property_name, value) VALUES (?, ?, ?) " +
                                    "ON DUPLICATE KEY UPDATE value=?");
                    for (Map.Entry<String, Object> property : device.getProperties().entrySet()) {
                        insertUpdateProperties.setString(1, deviceId.toString());
                        insertUpdateProperties.setString(2, property.getKey());
                        insertUpdateProperties.setObject(3, property.getValue());
                        insertUpdateProperties.setObject(4, property.getValue());
                        insertUpdateProperties.addBatch();
                    }
                    insertUpdateProperties.executeBatch();
                }

                connection.commit();

            } catch (Exception ex) {

                logger.debug("something was wrong during update, rollback will be performed");
                MysqlUtils.rollbackTransaction("update", connection);
                throw new IllegalException(DATABASE_ACCESS, format(
                        "Something was wrong during update, error %s", ex.getMessage()));
            }
        }
    }

    @Override
    @SneakyThrows
    public void drop(String deviceId) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                logger.debug(format("Performing a delete device id: %s", deviceId));

                final PreparedStatement deleteDevice = connection.prepareStatement(
                        "DELETE FROM " + NAMESPACE + ".device WHERE device_id = ?");
                deleteDevice.setString(1, deviceId);
                deleteDevice.execute();

                connection.commit();

            } catch (Exception ex) {

                logger.debug("something was wrong during delete, rollback will be performed");
                MysqlUtils.rollbackTransaction("delete", connection);
                throw new IllegalException(DATABASE_ACCESS, format(
                        "Something was wrong during delete, error %s", ex.getMessage()));
            }
        }
    }

    @Override
    @SneakyThrows
    public Optional<Device> select(String deviceId) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                logger.debug(format("Performing a select device id: %s", deviceId));

                PreparedStatement selectStatement = connection.prepareStatement(
                        "SELECT * FROM " + NAMESPACE + ".device  WHERE device_id = ?");
                selectStatement.setString(1, deviceId);
                final ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    return of(Device
                            .builder()
                            .deviceId(UUID.fromString(deviceId))
                            .fwVersion(resultSet.getString("fw_version"))
                            .partNumber(resultSet.getString("part_number"))
                            .lasUpdate(new DateTime(resultSet.getTimestamp("last_update")))
                            .manufacture(resultSet.getString("manufacture"))
                            .properties(getProperties(deviceId))
                            .owners(getUsers(deviceId))
                            .build());
                }
                return empty();

            } catch (Exception ex) {

                throw new IllegalException(DATABASE_ACCESS, format("Error selecting table, error %s", ex.getMessage()));
            }
        }
    }

    @Override
    @SneakyThrows
    public boolean isExists(String deviceId) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                logger.debug(format("Performing a select to check if device exists, device id: %s", deviceId));

                PreparedStatement selectStatement = connection.prepareStatement(
                        "SELECT device_id FROM " + NAMESPACE + ".device WHERE device_id = ?");
                selectStatement.setString(1, deviceId);
                final ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    return true;
                }
                return false;

            } catch (Exception ex) {

                throw new IllegalException(DATABASE_ACCESS, format("Error selecting table, error %s", ex.getMessage()));
            }
        }
    }

    @SneakyThrows
    private Map<String, Object> getProperties(String deviceId) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                logger.debug(format("Performing a select in property table with device id: %s", deviceId));

                final Map<String, Object> properties = new HashMap<>();

                PreparedStatement selectStatement = connection.prepareStatement(
                        "SELECT * FROM " + NAMESPACE + ".properties WHERE device_id = ?");
                selectStatement.setString(1, deviceId);
                final ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    properties.put(resultSet.getString("property_name"), resultSet.getObject("value"));
                }
                return properties;

            } catch (Exception ex) {

                throw new IllegalException(DATABASE_ACCESS, format("Error selecting table, error %s", ex.getMessage()));
            }
        }
    }

    @SneakyThrows
    private List<String> getUsers(String deviceId) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                logger.debug(format("Performing a select in Link users with device id: %s", deviceId));

                final List<String> users = new ArrayList<>();

                PreparedStatement selectStatement = connection.prepareStatement(
                        "SELECT username FROM " + NAMESPACE + ".owner_device_relationship WHERE device_id = ?");
                selectStatement.setString(1, deviceId);
                final ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    users.add(resultSet.getString("username"));
                }
                return users;

            } catch (Exception ex) {

                throw new IllegalException(DATABASE_ACCESS, format("Error selecting table, error %s", ex.getMessage()));
            }
        }
    }
}
