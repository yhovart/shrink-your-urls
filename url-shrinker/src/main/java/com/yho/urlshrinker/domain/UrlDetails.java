package com.yho.urlshrinker.domain;

import java.net.URL;
import java.util.UUID;

public record UrlDetails(UUID id, URL originalUrl, String shortCode) {
    
}
