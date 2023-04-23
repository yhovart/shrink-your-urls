package com.yho.urlshrinker.shrinker;

import java.net.URL;
import java.util.UUID;


public class RandomShrinker implements UrlShrinker {

    @Override
    public String shrink(URL original) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 9);
    }
    

}
