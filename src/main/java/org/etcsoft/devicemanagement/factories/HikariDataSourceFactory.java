package org.etcsoft.devicemanagement.factories;

import com.zaxxer.hikari.HikariDataSource;
import org.etcsoft.devicemanagement.Config.MysqlConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static java.lang.String.format;

@Configuration
public class HikariDataSourceFactory {

    @Autowired
    private MysqlConfig mysqlConfig;

    @Bean(destroyMethod = "close")
    @Primary
    public HikariDataSource createDatasource() {
        return getDatasource(mysqlConfig);
    }

    public static HikariDataSource getDatasource(MysqlConfig mysqlConfig) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl(format("jdbc:mysql://%s:%d/", mysqlConfig.getHost(), mysqlConfig.getPort()));
        dataSource.setUsername(mysqlConfig.getUser());
        dataSource.setPassword(mysqlConfig.getPasswd());
        dataSource.setAutoCommit(false);
        return dataSource;
    }
}
