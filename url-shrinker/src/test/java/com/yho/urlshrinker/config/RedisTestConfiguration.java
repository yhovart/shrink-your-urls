package com.yho.urlshrinker.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.redis.connection.RedisServer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@TestConfiguration
public class RedisTestConfiguration {
    
    private RedisServer redisServer;

    public RedisTestConfiguration(RedisProperties redisProperties) {
        this.redisServer = new RedisServer(redisProperties.getHost(), redisProperties.getPort());
    }

    @PostConstruct
    public void postConstruct() {
        // redisServer.
    }

    @PreDestroy
    public void preDestroy() {
        // redisServer.stop();
    }
}
