package com.yho.urlshrinker.exception;

import java.util.UUID;

public class UrlDetailsNotFound extends NotFoundException {
    
    public UrlDetailsNotFound(UUID id) {
        super("urls",  id.toString());
    }

}
