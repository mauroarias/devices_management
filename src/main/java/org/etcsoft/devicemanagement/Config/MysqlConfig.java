package org.etcsoft.devicemanagement.Config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class MysqlConfig {
    @Value("${mysql.username}")
    private String user;
    @Value("${mysql.password}")
    private String passwd;
    @Value("${mysql.host}")
    private String host;
    @Value("${mysql.port}")
    private int port;

    public static MysqlConfig getMysqlConfig(String mysqlUser,
                                             String mysqlPasswd,
                                             String mysqlHost,
                                             int mysqlPort) {
        MysqlConfig mysqlConfig = new MysqlConfig();
        mysqlConfig.host = mysqlHost;
        mysqlConfig.passwd = mysqlPasswd;
        mysqlConfig.port = mysqlPort;
        mysqlConfig.user = mysqlUser;
        return mysqlConfig;
    }
}
