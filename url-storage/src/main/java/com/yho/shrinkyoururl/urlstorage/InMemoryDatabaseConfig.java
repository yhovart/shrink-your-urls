package com.yho.shrinkyoururl.urlstorage;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InMemoryDatabaseConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server inMemoryH2DbServer() throws SQLException {

        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9090");
    }

}
