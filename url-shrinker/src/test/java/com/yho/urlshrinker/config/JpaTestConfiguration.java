package com.yho.urlshrinker.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@TestConfiguration
@EntityScan("com.yho.urlshrinker")
@EnableJpaRepositories("com.yho.urlshrinker")
@ComponentScan("com.yho.urlshrinker")
@EnableTransactionManagement
public class JpaTestConfiguration {
    
}
