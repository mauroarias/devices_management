package org.etcsoft.devicemanagement.repository;

import lombok.SneakyThrows;
import org.etcsoft.devicemanagement.model.Device;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static java.lang.String.format;
import static org.etcsoft.devicemanagement.repository.PropertyrepoMysql.ResourceTypes.DEVICE;

/**
 * Created by mauro on 22/04/16.
 */
public class DeviceRepoMysqlImpl extends AbstractMysql implements DeviceRepo {

    private final static String COLUMN_NAME = "device_name";
    private final static String COLUMN_MANUFACTURE = "manufacture";
    private final static String COLUMN_PART_NUMBER = "part_number";
    private final static String COLUMN_FW_VERSION = "fw_version";
    private final static String COLUMN_LAST_UPDATE = "last_update";
    private final static String COLUMN_ENABLED = "enabled";

    private final static String TABLE_NAME = "device";

    private final static String INSERT_QUERY =
            format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) %s",
                    TABLE_NAME,
                    COLUMN_NAME,
                    COLUMN_MANUFACTURE,
                    COLUMN_PART_NUMBER,
                    COLUMN_FW_VERSION,
                    COLUMN_LAST_UPDATE,
                    COLUMN_ENABLED,
                    "VALUES (?, ?, ?, ?, ?, ?)");
    private final static String DELETE_QUERY =
            format("DELETE from %s WHERE deviceName = ?", TABLE_NAME);
    private final static String SELECT_QUERY =
            format("SELECT * FROM %s Where deviceName = ?", TABLE_NAME);
    private final static String SELECT_ALL_QUERY = format("SELECT * FROM %s", TABLE_NAME);

    @Autowired
    @SneakyThrows
    public DeviceRepoMysqlImpl(DatabaseConfig mysqlConfig) throws Exception {
        super(mysqlConfig);
    }

    @Override
    public void insert(Device device) {

        PreparedStatement insertStatement = null;

        try {
            insertStatement = getConnector().prepareStatement(INSERT_QUERY);

            insertStatement.setString(1, device.getDeviceName());
            insertStatement.setString(2, device.getManufacture());
            insertStatement.setString(3, device.getPartNumber());
            insertStatement.setString(4, device.getFwVersion());
            insertStatement.setTimestamp(5, new Timestamp(device.getLasUpdate().getMillis()));
            insertStatement.setBoolean(6, device.getEnabled());
            insertStatement.execute();

            getPropertyRepo().insert(
                    device.getProperties(),
                    device.getDeviceName(),
                    DEVICE,
                    getConnector()
            );

            statementCommit();

        } catch(SQLException ex) {

            getLogger().error(
                    format("Error loading device: %s, %s",
                            device.toString(),
                            ex.getMessage()));

            rollbackTransaction();

            throw new IllegalStateException(ex.getMessage());

        } finally {

            closeTransaction(insertStatement);
        }
    }

    @Override
    public void update(Device newDevice, String deviceName) {
        if(somethingToUpdate(newDevice)) {

            Optional<Device> ref = select(deviceName);

            ref.ifPresent(device -> insert(
                    Device.builder()
                            .deviceName(deviceName)
                            .enabled(
                                    newDevice.getEnabled() == null ?
                                            device.getEnabled() :
                                            newDevice.getEnabled())
                            .fwVersion(
                                    newDevice.getFwVersion() == null ?
                                            device.getFwVersion() :
                                            newDevice.getFwVersion())
                            .lasUpdate(
                                    newDevice.getLasUpdate() == null ?
                                            device.getLasUpdate() :
                                            newDevice.getLasUpdate())
                            .manufacture(
                                    newDevice.getManufacture() == null ?
                                            device.getManufacture() :
                                            newDevice.getManufacture())
                            .partNumber(
                                    newDevice.getPartNumber() == null ?
                                            device.getPartNumber() :
                                            newDevice.getPartNumber())
                            .properties(newDevice.getProperties())
                            .build()));
        }
    }

    @Override
    public void delete(String deviceName) {

        PreparedStatement deleteStatement = null;

        try {
            deleteStatement = getConnector().prepareStatement(DELETE_QUERY);

            deleteStatement.setString(1, deviceName);
            deleteStatement.execute();

            getPropertyRepo().deleteProperties(deviceName, DEVICE, getConnector());

            statementCommit();

        } catch(SQLException ex) {

            getLogger().error(
                    format("Error deleting device: %s, %s",
                            deviceName,
                            ex.getMessage()));

            rollbackTransaction();

            throw new IllegalStateException(ex.getMessage());

        } finally {

            closeTransaction(deleteStatement);
        }
    }

    @Override
    @SneakyThrows
    public Optional<Device> select(String deviceName) {

        PreparedStatement selectStatement = getConnector().prepareStatement(SELECT_QUERY);

        selectStatement.setString(1, deviceName);

        return selectQuery(selectStatement).stream().findFirst();
    }

    @Override
    @SneakyThrows
    public List<Device> selectAll() {

        return selectQuery(getConnector().prepareStatement(SELECT_ALL_QUERY));
    }

    @SneakyThrows
    private List<Device> selectQuery(PreparedStatement selectStatement) {

        List<Device> devices = new ArrayList<>();

        try {
            getConnector().setAutoCommit(true);

            ResultSet result = selectStatement.executeQuery();

            while(result.next()) {
                String deviceName = result.getString(COLUMN_NAME);

                devices.add(org.etcsoft.devicemanagement.model.Device.builder()
                        .deviceName(deviceName)
                        .enabled(result.getBoolean(COLUMN_ENABLED))
                        .fwVersion(result.getString(COLUMN_FW_VERSION))
                        .lasUpdate(new DateTime(result.getTimestamp(COLUMN_LAST_UPDATE)))
                        .manufacture(result.getString(COLUMN_MANUFACTURE))
                        .partNumber(result.getString(COLUMN_PART_NUMBER))
                        .properties(getPropertyRepo().getProperties(getConnector(), deviceName, DEVICE))
                        .build());
            }

            return devices;
        } finally {

            closeTransaction(selectStatement);
            getConnector().setAutoCommit(isAutocommit());
        }
    }

    private boolean somethingToUpdate(Device device) {
        return device.getEnabled() != null ||
                device.getFwVersion() != null ||
                device.getLasUpdate() != null ||
                device.getManufacture() != null ||
                device.getPartNumber() != null;

    }
}
