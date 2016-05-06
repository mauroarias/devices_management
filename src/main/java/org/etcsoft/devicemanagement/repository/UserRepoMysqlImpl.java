package org.etcsoft.devicemanagement.repository;

import lombok.SneakyThrows;
import org.etcsoft.devicemanagement.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.etcsoft.devicemanagement.repository.PropertyrepoMysql.ResourceTypes.USER;

/**
 * Created by mauro on 04/05/16.
 */
public class UserRepoMysqlImpl extends AbstractMysql implements UserRepo {

    private final static String COLUMN_USER = "user";
    private final static String COLUMN_PASSWORD = "password";

    private final static String TABLE_NAME = "user";

    private final static String INSERT_QUERY =
            format("INSERT INTO %s (%s, %s) %s",
                    TABLE_NAME,
                    COLUMN_USER,
                    COLUMN_PASSWORD,
                    "VALUES (?, ?)");
    private final static String DELETE_QUERY =
            format("DELETE from %s WHERE %s = ?",
                    TABLE_NAME,
                    COLUMN_USER);
    private final static String SELECT_QUERY =
            format("SELECT * FROM %s Where %s = ?",
                    TABLE_NAME,
                    COLUMN_USER);
    private final static String SELECT_ALL_QUERY =
            format("SELECT * FROM %s",
                    TABLE_NAME);


    @Autowired
    @SneakyThrows
    public UserRepoMysqlImpl(DatabaseConfig mysqlConfig) throws Exception {
        super(mysqlConfig);
    }

    @Override
    public void insert(User user) {

        PreparedStatement insertStatement = null;

        try {
            insertStatement = getConnector().prepareStatement(INSERT_QUERY);

            insertStatement.setString(1, user.getUser());
            insertStatement.setString(2, user.getPasswd());
            insertStatement.execute();

            getPropertyRepo().insert(
                    user.getProperties(),
                    user.getUser(),
                    USER,
                    getConnector()
            );

            for(String deviceName : user.getDeviceNames()) {

                getDevicesByUserRepo().insert(getConnector(), user.getUser(), deviceName);
            }

            statementCommit();

        } catch(SQLException ex) {

            getLogger().error(
                    format("Error loading user: %s, %s",
                            user.toString(),
                            ex.getMessage()));

            rollbackTransaction();

            throw new IllegalStateException(ex.getMessage());

        } finally {

            closeTransaction(insertStatement);
        }
    }

    @Override
    public void update(User device, String user) {

    }

    @Override
    public void delete(String user) {

        PreparedStatement deleteStatement = null;

        try {
            deleteStatement = getConnector().prepareStatement(DELETE_QUERY);

            deleteStatement.setString(1, user);
            deleteStatement.execute();

            getPropertyRepo().deleteProperties(user, USER, getConnector());

            statementCommit();

        } catch(SQLException ex) {

            getLogger().error(
                    format("Error deleting device: %s, %s",
                            user,
                            ex.getMessage()));

            rollbackTransaction();

            throw new IllegalStateException(ex.getMessage());

        } finally {

            closeTransaction(deleteStatement);
        }
    }

    @Override
    @SneakyThrows
    public Optional<User> select(String user) {

        PreparedStatement selectStatement = getConnector().prepareStatement(SELECT_QUERY);

        selectStatement.setString(1, user);

        return selectQuery(selectStatement).stream().findFirst();
    }

    @Override
    @SneakyThrows
    public List<User> selectAll() {
        return selectQuery(getConnector().prepareStatement(SELECT_ALL_QUERY));
    }

    @SneakyThrows
    private List<User> selectQuery(PreparedStatement selectStatement) {

        List<User> users = new ArrayList<>();

        try {
            getConnector().setAutoCommit(true);

            ResultSet result = selectStatement.executeQuery();

            while(result.next()) {
                String user = result.getString(COLUMN_USER);

                users.add(User.builder()
                        .user(user)
                        .passwd(result.getString(COLUMN_PASSWORD))
                        .properties(getPropertyRepo().getProperties(getConnector(), user, USER))
                        .deviceNames(getDevicesByUserRepo().getDevices(getConnector(), user))
                        .build());
            }

            return users;
        } finally {

            closeTransaction(selectStatement);
            getConnector().setAutoCommit(isAutocommit());
        }
    }
}
