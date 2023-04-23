package com.yho.urlshrinker.service;

import java.util.UUID;
import java.util.stream.StreamSupport;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yho.urlshrinker.domain.UrlDetails;
import com.yho.urlshrinker.entity.UrlEntity;
import com.yho.urlshrinker.exception.UrlCodeNotFound;
import com.yho.urlshrinker.exception.UrlDetailsNotFound;
import com.yho.urlshrinker.repository.UrlRepository;
import com.yho.urlshrinker.shrinker.UrlShrinker;

@Service
public class UrlService {

    private final UrlRepository repository;

    private final UrlShrinker shrinker;

    public UrlService(UrlRepository repository, UrlShrinker shrinker) {
        this.repository = repository;
        this.shrinker = shrinker;
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


    // TODO transaction avec reddis?
    // TODO il reste possible d'avoir une collision si 2 appels paralleles obtiennent le meme code unique
    // le cas est extremenent hautement improbable fonction de l'entropie mais on peut l'adresser 
    // idealement la maniere de l'adresser est scalable (i.e. un synchronized et doouble check ne sont pas suffisants)
    @Transactional
    public UrlDetails createUrl(URL url) {
        var entity = new UrlEntity();
        entity.setId(UUID.randomUUID());
        entity.setUrl(url);
        entity.setCode(getUniqueCode(url));
        return toUrlDetails(this.repository.save(entity));
    }


    private String getUniqueCode(URL url) {
        String code = null; 
        do {
            code = shrinker.shrink(url);
        } while (repository.findByCode(code).isPresent());
        return code;
    }

    private UrlDetails toUrlDetails(UrlEntity entity) {
        return new UrlDetails(entity.getId(), entity.getUrl(), entity.getCode());
    }

    public Optional<UrlDetails> findUrlDetails(URL url) {
        return this.repository.findByOriginalUrl(url).map(this::toUrlDetails);
    }

    public Optional<UrlDetails> findUrlDetailsByCode(String code) {
        return this.repository.findByCode(code).map(this::toUrlDetails);
    }

    public UrlDetails getUrlDetailsByCode(String code) {
        return findUrlDetailsByCode(code).orElseThrow(
            () -> new UrlCodeNotFound(code)
        );
    }

    
}
