package com.yho.urlshrinker.repository;

import java.net.URL;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.yho.urlshrinker.entity.UrlEntity;

public interface UrlRepository extends CrudRepository<UrlEntity, UUID> {

    Optional<UrlEntity> findByCode(String code);

    Optional<UrlEntity> findByOriginalUrl(URL url);


}
