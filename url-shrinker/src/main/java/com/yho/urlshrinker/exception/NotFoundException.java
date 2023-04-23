package com.yho.urlshrinker.exception;

// Unchecked exception so that applying the default behaviour we want (throwing the exception up to the spring interceptor) is effortlessly.
// NDC: Note that even Java creator thinks now checked exception was a bad idea (Also follow modern approcaches like kotlin's that makes all exceptions unchecked)
public class NotFoundException extends RuntimeException {  // Note: no record classes for exception as they are designed to be extendable

    private String id;
    private String resourceType;

    public NotFoundException(String resourceType, String id){
        super(getMessage(resourceType, id));
        this.resourceType = resourceType;
        this.id = id;
    }

    public NotFoundException(String resourceType, String id, Throwable cause){
        super(getMessage(resourceType, id), cause);
        this.resourceType = resourceType;
        this.id = id;
    }

    public NotFoundException(String resourceType, String id, Throwable cause,  boolean enableSuppression, boolean writableStackTrace){
        super(getMessage(resourceType, id), cause, enableSuppression, writableStackTrace);
        this.resourceType = resourceType;
        this.id = id;
    }

    public String getId() {
        return id;
    }
    
    public String getResourceType() {
        return resourceType;
    }

    private static String getMessage(String resourceType, String id) {
        return String.format("Resource %s with id %s not found.", resourceType, id);
    }
}
