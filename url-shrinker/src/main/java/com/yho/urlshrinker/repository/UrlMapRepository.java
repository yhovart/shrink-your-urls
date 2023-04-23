package com.yho.urlshrinker.repository;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Repository;

import com.yho.urlshrinker.entity.UrlEntity;

/**
 * In memory hasmap repository; for dev and test purpose only.
 */
// TODO: Delete me once POC is done
@Repository
public class UrlMapRepository implements UrlRepository {

    Map<UUID, UrlEntity> URLS = new HashMap<>();
    Map<String, UrlEntity> URLS_BY_CODE = new HashMap<>();
    Map<String, UrlEntity> URLS_BY_URL = new HashMap<>();

    @Override
    public <S extends UrlEntity> S save(S entity) {
        URLS.put(entity.getId(), entity);
        URLS_BY_CODE.put(entity.getCode(), entity);
        URLS_BY_URL.put(entity.getUrl().toString(), entity);
        return entity;
    }

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
    public Optional<UrlEntity> findByOriginalUrl(URL url) {
        return Optional.ofNullable(URLS_BY_URL.get(url.toString()));
    }


}
