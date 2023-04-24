package com.yho.urlshrinker.config;

import java.net.URL;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;

import com.yho.urlshrinker.shrinker.HashRandomShrinker;
import com.yho.urlshrinker.shrinker.UUIDRandomShrinker;
import com.yho.urlshrinker.shrinker.UrlShrinker;

@Configuration
public class ShrkinkingConfig {

    @Bean
    @Profile("random")
    UrlShrinker basicShrinker(){
        return new UUIDRandomShrinker();
    }
    

    @Bean
    @Profile("hash")
    UrlShrinker hashShrinker(){
        return new HashRandomShrinker();
    }

    @Bean
    @Profile("time")
    UrlShrinker timeShrinker(){
        // bad but to illustrate the possibility to switch code generation strategy at runtime
        return new UrlShrinker() {

            @Override
            public String shrink(@NonNull URL original) {
                return String.valueOf(System.currentTimeMillis());
            }
            
        };
    }

}
