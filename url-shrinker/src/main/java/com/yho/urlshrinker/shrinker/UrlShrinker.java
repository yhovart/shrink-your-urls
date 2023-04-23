package com.yho.urlshrinker.shrinker;

import java.net.URL;

//@FunctionalInterface // NDC: no usage for it as a FuncitonalInterface at the moment
public interface UrlShrinker {
    
    String shrink(URL original);

}
