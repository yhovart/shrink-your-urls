package com.yho.urlshrinker.shrinker;

import java.net.URL;
import java.util.UUID;

import org.springframework.lang.NonNull;


public class UUIDRandomShrinker implements UrlShrinker {

    @Override
    public String shrink(@NonNull URL original) {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    

}
