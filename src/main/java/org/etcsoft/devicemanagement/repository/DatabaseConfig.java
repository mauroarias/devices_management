package org.etcsoft.devicemanagement.repository;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import static java.lang.String.format;

/**
 * Created by mauro on 26/04/16.
 */
@Value
@Builder
public class DatabaseConfig {
    private static final int MIN_PORT = 0;
    private static final int MAX_PORT = 65535;

    private final String hostname;
    private final int port;
    private final String databaseName;
    private final String user;
    private final String password;
    private final long connectionTimeout;
    private final boolean autoCommit;

    private DatabaseConfig(
            String hostname,
            int port,
            String databaseName,
            String user,
            String password,
            long connectionTimeout,
            boolean autoCommit) {

        if(StringUtils.isBlank(hostname)) {
            throw new IllegalArgumentException("Hostname cannot be empty or null");
        }

        if(StringUtils.isBlank(user)) {
            throw new IllegalArgumentException("user cannot be empty or null");
        }

        if(StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("password cannot be empty or null");
        }

        if(port < MIN_PORT || port > MAX_PORT) {
            throw new IllegalArgumentException(format(
                    "%d is not a valid port number it must be between %d to %d", port, MIN_PORT, MAX_PORT));
        }

        if(connectionTimeout == 0){
            connectionTimeout = 6000;
        }

        if(port == 0) {
            port = 3306;
        }

        this.hostname = hostname;
        this.port = port;
        this.databaseName = databaseName;
        this.user = user;
        this.password = password;
        this.connectionTimeout = connectionTimeout;
        this.autoCommit = autoCommit;
    }
}
