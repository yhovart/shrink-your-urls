package com.yho.urlshrinker.repository;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.yho.urlshrinker.entity.UrlEntity;
import com.yho.urlshrinker.exception.NonUniqueCodeException;

/**
 * In memory hasmap repository; for dev and test purpose only.
 * Prevents horizontal scaling.
 */
@Repository
@Profile("memory")
// NDC : Ce repo a servi a sketché rapidement l'API sans avoir a prenre de décision sur la persistence.
//  Il n'a plus d'utilité maintenant.
//  Il a été conservé car il contient plus de code et permet donc des tests unitires sur une classe plus complete que via un repo JPA
//  Il permet d'illuster facilement l'utilisation des profils pour changer le comportement runtime qui pourrait etre utile en cas d'essais comparatifs.
public class UrlMapRepository implements UrlRepository {


    private Map<UUID, UrlEntity> URLS = new HashMap<>();
    private Map<String, UrlEntity> URLS_BY_CODE = new HashMap<>();
    private Map<String, UrlEntity> URLS_BY_URL = new HashMap<>();
    // NDC : Repository = singleton; no need for static maps

    @Override
    public <S extends UrlEntity> S save(S entity) {
        var existing = URLS.get(entity.getId());
        URLS.put(entity.getId(), entity);
        URLS_BY_CODE.put(entity.getCode(), entity);
        URLS_BY_URL.put(entity.getUrl().toString(), entity);

        // init la date de modif et le modifierdBy aux donnees de creation permet d'avoir des champs non nullable
        if (existing == null){
            entity.setCreationDate(LocalDateTime.now());
            entity.setCreator("creator");
            entity.setModifiedBy("creator");
        } 
        entity.setModifiedDate(LocalDateTime.now());
        entity.setModifiedBy("modifier");
        return entity;
    }

    public <S extends UrlEntity> S saveIfCodeIsUnique(S entity) throws NonUniqueCodeException {
        if (URLS_BY_CODE.containsKey(entity.getCode())){
            throw new  NonUniqueCodeException(entity.getCode(), "Code already used"); 
        }
        return this.save(entity);
    };

    @Override
    public <S extends UrlEntity> Iterable<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::save);
        return entities;
    }

    @Override
    public Optional<UrlEntity> findById(UUID id) {
        return Optional.ofNullable(URLS.get(id));
    }

    @Override
    public boolean existsById(UUID id) {
        return URLS.containsKey(id);
    }

    @Override
    public Iterable<UrlEntity> findAll() {
        return URLS.values();
    }

    @Override
    public Iterable<UrlEntity> findAllById(Iterable<UUID> ids) {
        return StreamSupport.stream(ids.spliterator(), false)
            .map(this::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }

    @Override
    public long count() {
        return URLS.size();
    }

    @Override
    public void deleteById(UUID id) {
        URLS.remove(id);
    }

    @Override
    public void delete(UrlEntity entity) {
       URLS.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends UrlEntity> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        URLS.clear();
    }

    @Override
    public Optional<UrlEntity> findByCode(String code) {
        return Optional.ofNullable(URLS_BY_CODE.get(code));
    }

    @Override
    public Optional<UrlEntity> findByUrl(URL url) {
        return Optional.ofNullable(URLS_BY_URL.get(url.toString()));
    }


}
