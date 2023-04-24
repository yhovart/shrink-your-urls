package com.yho.urlshrinker.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.yho.urlshrinker.domain.LongUrl;
import com.yho.urlshrinker.domain.UrlDetails;
import com.yho.urlshrinker.exception.InvalidUrlException;
import com.yho.urlshrinker.service.UrlService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;


// TODO security
/**
 * REST API that exposes the urls data.
 * Mays be called by authorized users / apps.
 */
@RestController
@RequestMapping("/api/v1/urls")
@Validated 
public class UrlController {
    

    // NDC: wanted to skip lombok to evaluate its usefulness now that Java has record classes; so no @Sl4jf
    private final Logger logger = LoggerFactory.getLogger(UrlController.class);
    // note: Baeldung examples does not declare the logger as a static instance; a bit surprising but 
    // RestController being singletons it is quite equivalent memory-wise, so why not (may be a typo though).

    private final UrlService urlService;
    

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }


    @GetMapping("/search")
    // should be a paginated search at least in real life
    public List<UrlDetails> getUrls() {
        logger.debug("Accessing all urls");
        return urlService.getUrls();
    }

    @GetMapping(value="/{id}")
    public UrlDetails getUrl(
        @PathVariable 
        @org.hibernate.validator.constraints.UUID 
        @NotNull @NotEmpty String id) {
        logger.debug("Accessing detail of url {}", id);
        return urlService.getUrl(UUID.fromString(id));
    }
    

    @PostMapping
    public ResponseEntity<UrlDetails> addUrl(@Valid @RequestBody LongUrl longUrl) {
        // url may contain sensitive data; we dont log it
        logger.debug("Generating short url");

        
        var url = getAsUrl(longUrl.url()); 

        // POST usually gives 201
        // but it is asked that creating an already existing short urgives the same response, not an error or a duplicate
        // to illustrate that qe respond 200 on existing ones and regular 201 on new ones
        var existing = urlService.findUrlDetails(url);
        
        if (existing.isPresent()) {
            logger.info("Trying to save an already existing url; returning {}", existing.get().id());
            return ResponseEntity.ok(existing.get());
        }
        return ResponseEntity.created(null).body(urlService.createUrl(url));
    }

    @DeleteMapping
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void clear() {
        logger.warn("Clearing all urls!!");
        urlService.clear();
    }


    private @NonNull URL getAsUrl(@NonNull String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new InvalidUrlException(url, e);
        }
    }

    
}