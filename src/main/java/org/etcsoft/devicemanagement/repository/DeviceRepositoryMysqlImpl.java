package org.etcsoft.devicemanagement.repository;

import lombok.SneakyThrows;
import org.etcsoft.devicemanagement.model.Device;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.PreparedStatement;

/**
 * Created by mauro on 22/04/16.
 */
public class DeviceRepositoryMysqlImpl extends AbstractMysql implements DeviceRepository {

    private static String TABLE_NAME = "device";
    private static String INSERT_QUERY =
            String.format("INSERT INTO %s %s %s",
                    TABLE_NAME,
                    "(deviceName, deviceId, manufacture, partNumber, fwVersion, lasUpdate, enabled)",
                    "VALUES (?, ?, ?, ?, ?, ?, ?)");
    private final PreparedStatement insertStatement;

    @Autowired
    @SneakyThrows
    public DeviceRepositoryMysqlImpl(MysqlConnection mysqlConnection) {
        super(mysqlConnection);
        insertStatement = getConnector().prepareStatement(INSERT_QUERY);
    }

    @Override
    @SneakyThrows
    public void insert(Device device) {
        insertStatement.setString(1, device.getDeviceName());

        insertStatement.execute();
    }

    @Override
    public void update(Device device, String deviceId) {

    }

    @Override
    public void drop(String deviceId) {

    }

    @Override
    public void select(String deviceId) {

    }
}
