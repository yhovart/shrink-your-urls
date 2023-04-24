package com.yho.urlshrinker.shrinker;

import java.net.URL;

import org.springframework.lang.NonNull;

//@FunctionalInterface // NDC: no usage for it as a FunctionalInterface at the moment
public interface UrlShrinker {
    
    String shrink(@NonNull URL original);

}
