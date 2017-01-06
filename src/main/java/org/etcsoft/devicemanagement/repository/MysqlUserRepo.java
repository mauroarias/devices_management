package org.etcsoft.devicemanagement.repository;

import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.etcsoft.devicemanagement.model.Exceptions.IllegalException;
import org.etcsoft.devicemanagement.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.etcsoft.devicemanagement.model.Enums.ErrorCodes.DATABASE_ACCESS;
import static org.etcsoft.devicemanagement.repository.Constants.NAMESPACE;

@Component
public final class MysqlUserRepo implements UserRepo {
    private final static Logger logger = Logger.getLogger(MysqlUserRepo.class);

    private final HikariDataSource dataSource;

    @Autowired
    public MysqlUserRepo(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @SneakyThrows
    public void insert(User user) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                logger.debug(format("Performing an insert username: %s", user.getUsername()));

                final PreparedStatement insertuser = connection.prepareStatement(
                        "INSERT INTO " + NAMESPACE + ".user " +
                                "(username, email, password, first_name, last_name) " +
                                "VALUES (?, ?, ?, ?, ?)");
                insertuser.setString(1, user.getUsername());
                insertuser.setString(2, user.getEmail());
                insertuser.setString(3, BCrypt.hashpw(user.getPasswd(), BCrypt.gensalt(12)));
                insertuser.setString(4, user.getFirstName());
                insertuser.setString(5, user.getLastName());
                insertuser.execute();

                final PreparedStatement insertOwnerLinks = connection.prepareStatement(
                        "INSERT INTO " + NAMESPACE + ".owner_device_relationship (device_id, username) VALUES (?, ?)");
                for(UUID deviceId : user.getDevices()) {
                    insertOwnerLinks.setString(1, deviceId.toString());
                    insertOwnerLinks.setString(2, user.getUsername());
                    insertOwnerLinks.addBatch();
                }
                insertOwnerLinks.executeBatch();

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
    public void update(User user, String username) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                logger.debug(format("Performing an update username: %s", username));

                final PreparedStatement updateDevice = connection.prepareStatement(
                        "UPDATE " + NAMESPACE + ".user " +
                        "SET email = COALESCE(?, email), " +
                            "password = COALESCE(?, password), " +
                            "first_name = COALESCE(?, first_name), " +
                            "last_name = COALESCE(?, last_name) " +
                        "WHERE username = ?");
                updateDevice.setString(1, user.getEmail());
                updateDevice.setString(2,
                        user.getPasswd() == null ?
                                null :
                                BCrypt.hashpw(user.getPasswd(), BCrypt.gensalt(12)));
                updateDevice.setString(3, user.getFirstName());
                updateDevice.setString(4, user.getLastName());
                updateDevice.setString(5, username);
                updateDevice.execute();

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
    public void drop(String username) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                logger.debug(format("Performing a delete username: %s", username));

                final PreparedStatement deleteDevice = connection.prepareStatement(
                        "DELETE FROM " + NAMESPACE + ".user WHERE username = ?");
                deleteDevice.setString(1, username);
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
    public Optional<User> select(String username) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                logger.debug(format("Performing a select username: %s", username));

                PreparedStatement selectStatement = connection.prepareStatement(
                        "SELECT * FROM " + NAMESPACE + ".user  WHERE username = ?");
                selectStatement.setString(1, username);
                final ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    return of(User
                            .builder()
                            .username(username)
                            .email(resultSet.getString("email"))
                            .firstName(resultSet.getString("first_name"))
                            .lastName(resultSet.getString("last_name"))
                            .devices(getDevices(username))
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
    public Boolean isExists(String username) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                logger.debug(format("Performing a select to check if user exists, username: %s", username));

                PreparedStatement selectStatement = connection.prepareStatement(
                        "SELECT username FROM " + NAMESPACE + ".user WHERE username = ?");
                selectStatement.setString(1, username);
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

    @Override
    @SneakyThrows
    public void updatePassword(String username, String password) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                logger.debug(format("Performing an update password username: %s", username));

                final PreparedStatement updateDevice = connection.prepareStatement(
                        "UPDATE " + NAMESPACE + ".user SET password = COALESCE(?, password) WHERE username = ?");
                updateDevice.setString(1, password == null ? null : BCrypt.hashpw(password, BCrypt.gensalt(12)));
                updateDevice.setString(2, username);
                updateDevice.execute();

                connection.commit();

            } catch (Exception ex) {

                logger.debug("something was wrong during update, rollback will be performed");
                MysqlUtils.rollbackTransaction("update", connection);
                throw new IllegalException(DATABASE_ACCESS, format(
                        "Something was wrong during update, error %s", ex.getMessage()));
            }
        }
    }

    @SneakyThrows
    private List<UUID> getDevices(String username) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                logger.debug(format("Performing a select in Link devices with username: %s", username));

                final List<UUID> deviceIds = new ArrayList<>();

                PreparedStatement selectStatement = connection.prepareStatement(
                        "SELECT device_id FROM " + NAMESPACE + ".owner_device_relationship WHERE username = ?");
                selectStatement.setString(1, username);
                final ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    deviceIds.add(UUID.fromString(resultSet.getString("device_id")));
                }
                return deviceIds;

            } catch (Exception ex) {

                throw new IllegalException(DATABASE_ACCESS, format("Error selecting table, error %s", ex.getMessage()));
            }
        }
    }
}
