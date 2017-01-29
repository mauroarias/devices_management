package org.etcsoft.devicemanagement.repository;

import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.etcsoft.devicemanagement.model.Exceptions.IllegalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

import static java.lang.String.format;
import static org.etcsoft.devicemanagement.model.Enums.ErrorCodes.DATABASE_ACCESS;
import static org.etcsoft.devicemanagement.repository.Constants.NAMESPACE;

@Component
public final class MysqlUserDeviceLinkRepo extends MysqlRepo implements UserDeviceLinkRepo {
    private final static Logger logger = Logger.getLogger(MysqlDeviceRepo.class);

    private final HikariDataSource dataSource;

    @Autowired
    public MysqlUserDeviceLinkRepo(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @SneakyThrows
    public void linkDevicesToUser(UUID deviceId, String username) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                logger.debug(format("Performing an insert device ids into an username: %s", username));

                final PreparedStatement insertOwnerLinks = connection.prepareStatement(
                        "INSERT INTO " + NAMESPACE + ".owner_device_relationship (device_id, username) VALUES (?, ?)");
                insertOwnerLinks.setString(1, deviceId.toString());
                insertOwnerLinks.setString(2, username);
                insertOwnerLinks.addBatch();

                connection.commit();

            } catch (Exception ex) {

                logger.debug("something was wrong during insert device ids into an username, rollback will be performed");
                rollbackTransaction("insert", connection);
                throw new IllegalException(DATABASE_ACCESS, format(
                        "Something was wrong during insert device ids into an username, error %s", ex.getMessage()));
            }
        }
    }

    @Override
    @SneakyThrows
    public void unLinkDevicesToUser(UUID deviceId, String username) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                logger.debug(format("Performing an delete device ids into an username: %s", username));

                final PreparedStatement insertOwnerLinks = connection.prepareStatement(
                        "DELETE " + NAMESPACE + ".owner_device_relationship WHERE device_id = ? AND username = ?");
                insertOwnerLinks.setString(1, deviceId.toString());
                insertOwnerLinks.setString(2, username);
                insertOwnerLinks.addBatch();

                connection.commit();

            } catch (Exception ex) {

                logger.debug("something was wrong during delete device ids into an username, rollback will be performed");
                rollbackTransaction("delete", connection);
                throw new IllegalException(DATABASE_ACCESS, format(
                        "Something was wrong during delete device ids into an username, error %s", ex.getMessage()));
            }
        }
    }
}
