package com.yho.urlshrinker.config;

//@Configuration
//@ConfigurationProperties(prefix = "spring.data.redis")
// note : no Record classes for @ConfigProperties
// TODO: why ?
public class RedisProperties {

    private final int port;
    
    private final String host;

    public RedisProperties(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

}