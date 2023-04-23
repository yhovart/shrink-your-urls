package com.yho.urlshrinker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yho.urlshrinker.shrinker.RandomShrinker;
import com.yho.urlshrinker.shrinker.UrlShrinker;

@Configuration
public class ShrkinkingConfig {

    @Bean
    UrlShrinker basicShrinker(){
        return new RandomShrinker();
    }
    
}
