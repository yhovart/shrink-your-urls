package com.yho.urlshrinker.exception;

public class UrlCodeNotFound extends NotFoundException {

    public UrlCodeNotFound(String code) {
        super("urls", code);
    }


}
