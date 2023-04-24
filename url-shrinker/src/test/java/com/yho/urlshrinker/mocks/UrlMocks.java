package com.yho.urlshrinker.mocks;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.UUID;

import com.yho.urlshrinker.domain.UrlDetails;
import com.yho.urlshrinker.entity.UrlEntity;

public class UrlMocks {

    private UrlMocks(){
        // this is intended
    }

    public static UrlEntity mockEntity() {
        return mockEntity(UUID.randomUUID(), UUID.randomUUID().toString().substring(0,9), "https://www.google.com?q=" + UUID.randomUUID());
    }

    public static UrlEntity mockEntity(String url) {
        return mockEntity(UUID.randomUUID(), UUID.randomUUID().toString().substring(0,9), url);
    }

    public static UrlEntity mockEntity(UUID id, String code, String url) {
        var result = new UrlEntity();
        result.setId(id);
        result.setCode(code);
        result.setCreationDate(LocalDateTime.now());
        result.setModifiedDate(LocalDateTime.now());
        result.setCreator("mock-creator");
        result.setModifiedBy("mock-creator");
        result.setUrl(asURL(url));
        return result;
    }

    public static UrlDetails mockDetails(UUID id, String code, String url){
        return new UrlDetails(id, asURL(url), code);
    }

    public static UrlDetails mockDetails(){
        return new UrlDetails(UUID.randomUUID(), asURL("http://wwww.bing.com/search=" + System.currentTimeMillis()), UUID.randomUUID().toString().substring(0,9));
    }

    private static URL asURL(String url){
        try{
           return new URL(url);
        }catch(MalformedURLException e){
            throw new IllegalArgumentException("mock url is invalid", e);
        }
    }
    
}
