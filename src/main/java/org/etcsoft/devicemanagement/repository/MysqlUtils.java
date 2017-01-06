package org.etcsoft.devicemanagement.repository;

import org.etcsoft.devicemanagement.model.Exceptions.IllegalException;

import java.sql.Connection;
import java.sql.SQLException;

import static java.lang.String.format;
import static org.etcsoft.devicemanagement.model.Enums.ErrorCodes.DATABASE_ACCESS;

final class MysqlUtils {

    static void rollbackTransaction(String transactionType, Connection connection) {
        try {
            connection.rollback();

        } catch (SQLException ex) {
            throw new IllegalException(DATABASE_ACCESS, format(
                    "Error trying to rollback transaction during %s, error %s", transactionType, ex.getMessage()));
        }
    }
}
