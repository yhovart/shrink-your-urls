package com.yho.urlshrinker.exception;

// Unchecked exception so that applying the default behaviour we want (throwing the exception up to the spring interceptor) is effortlessly.
// NDC: Note that even Java creator thinks now checked exception was a bad idea (Also follow modern approcaches like kotlin's that makes all exceptions unchecked)
public class InvalidUrlException extends RuntimeException { // Note: no record classes for exception as they are designed to be extendable
    private String url;

    public InvalidUrlException(String url, String message) {
        super(message);
        this.url = url;
    }

    public InvalidUrlException(String url, Throwable cause) {
        super(cause);
        this.url = url;
    }

    public InvalidUrlException(String url, String message, Throwable cause) {
        super(message, cause);
        this.url = url;
    }

    public InvalidUrlException(String url, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }


}
