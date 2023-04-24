package com.yho.urlshrinker.service;

import java.util.UUID;
import java.util.stream.StreamSupport;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yho.urlshrinker.domain.UrlDetails;
import com.yho.urlshrinker.entity.UrlEntity;
import com.yho.urlshrinker.exception.NonUniqueCodeException;
import com.yho.urlshrinker.exception.UrlCodeNotFound;
import com.yho.urlshrinker.exception.UrlDetailsNotFound;
import com.yho.urlshrinker.repository.UrlRepository;
import com.yho.urlshrinker.shrinker.UrlShrinker;

@Service
public class UrlService {

    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);
 
    private final UrlRepository repository;

    private final UrlShrinker shrinker;

    private int nbRetry;

    private int shortCodeLength;

    public UrlService(UrlRepository repository, UrlShrinker shrinker,
        @Value("${url-shrinker.retryOnNonUniqueCode:3}") int nbRetry,
        @Value("${url-shrinker.shortCodeLength:9}") int shortCodeLength) {
        this.repository = repository;
        this.shrinker = shrinker;
        this.nbRetry = nbRetry;
        this.shortCodeLength = shortCodeLength;
    }

    public List<UrlDetails> getUrls() {
        return StreamSupport.stream(this.repository.findAll().spliterator(), false).map(
            this::toUrlDetails
        ).toList();
    }

    public UrlDetails getUrl(UUID id) {
        return this.repository.findById(id).map(this::toUrlDetails).orElseThrow(
            () -> new UrlDetailsNotFound(id)
        );
    }


    private UrlDetails createUrlEntity(URL url) {
        var entity = new UrlEntity();
        entity.setId(UUID.randomUUID());
        entity.setUrl(url);
        entity.setCode(this.generateCode(url));

        var result = this.repository.saveIfUnique(entity);
        return toUrlDetails(result);
    }

    @Transactional
    public UrlDetails createUrl(URL url) {
        NonUniqueCodeException last = null;
        for(int i = 0; i <= nbRetry; i++){
            try{
                return createUrlEntity(url);
            }catch(NonUniqueCodeException e){
                last = e;
                logger.info("insert url {} failed, attempt {}", url, i+1);
                logger.debug("NonUniqueExceptionDetails", e);
            }
        }
        throw Optional.<RuntimeException>of(last).orElse(new IllegalArgumentException("retry should be positive or zero."));
    }

    private UrlDetails toUrlDetails(UrlEntity entity) {
        return new UrlDetails(entity.getId(), entity.getUrl(), entity.getCode());
    }

    public Optional<UrlDetails> findUrlDetails(URL url) {
        return this.repository.findByUrl(url).map(this::toUrlDetails);
    }

    public Optional<UrlDetails> findUrlDetailsByCode(String code) {
        return this.repository.findByCode(code).map(this::toUrlDetails);
    }

    public UrlDetails getUrlDetailsByCode(String code) {
        return findUrlDetailsByCode(code).orElseThrow(
            () -> new UrlCodeNotFound(code)
        );
    }


    private String generateCode(URL url){
        var shortCode = this.shrinker.shrink(url);
        var encoded = Base64.getUrlEncoder().encodeToString(shortCode.getBytes());
        if (encoded.length() > shortCodeLength){
            return encoded.substring(0, shortCodeLength);
        }
        return encoded;
    }


    public void clear() {
        repository.deleteAll();
    }

    
}
