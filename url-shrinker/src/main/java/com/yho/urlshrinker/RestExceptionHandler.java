package com.yho.urlshrinker;

import java.net.URI;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.yho.urlshrinker.exception.NotFoundException;

import jakarta.validation.ConstraintViolationException;

/**
 * Global Exception handler for the REST API.
 * Relies on the Problem API to transform
 */
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleRecordNotFoundException(NotFoundException ex, WebRequest request) {
  
      ProblemDetail body = ProblemDetail
          .forStatusAndDetail(HttpStatusCode.valueOf(404),ex.getLocalizedMessage());
      body.setType(URI.create("http://urlshrinker.com/errors/not-found"));
      body.setTitle("Resource Not found");
      body.setProperty("ressourceId", ex.getId());
      body.setProperty("resourceType", ex.getResourceType());
  
      return body;
    }

    
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
  
      ProblemDetail body = ProblemDetail
          .forStatusAndDetail(HttpStatusCode.valueOf(400),ex.getLocalizedMessage());
      body.setType(URI.create("http://urlshrinker.com/errors/bad-request"));
      body.setTitle("Bad Request");
      ex.getConstraintViolations().iterator().forEachRemaining(it -> {
        body.setProperty("error", it.getMessage());  
      }
      );
      return body;
    }


    
}
