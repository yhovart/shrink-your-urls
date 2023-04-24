package com.yho.urlshrinker.exception;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class NonUniqueCodeException extends ErrorResponseException {

    private String code;

    public NonUniqueCodeException(String code, String message, Throwable cause){
        super(HttpStatus.INTERNAL_SERVER_ERROR, asProblemDetail(message), cause);
        this.code = code;
    }
   
    public NonUniqueCodeException(String code, String message){
        this(code, message, null);
    }

    public String getCode() {
        return code;
    }

    private static ProblemDetail asProblemDetail(String message) {
        ProblemDetail problemDetail = ProblemDetail
          .forStatusAndDetail(HttpStatusCode.valueOf(500), message);
          problemDetail.setType(URI.create("http://urlshrinker.com/errors/no-short-code"));
          problemDetail.setTitle("No short code available.");
          problemDetail.setProperty("Api-Error-Code", "URL-0001");
        return problemDetail;
    }
}
