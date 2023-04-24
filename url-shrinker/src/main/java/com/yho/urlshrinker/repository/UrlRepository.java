package com.yho.urlshrinker.repository;

import java.net.URL;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.yho.urlshrinker.entity.UrlEntity;
import com.yho.urlshrinker.exception.NonUniqueCodeException;

/**
 * Provides abstraction for the different repositories that could hndle the storage of UrlDetails.
 */
@NoRepositoryBean
public interface UrlRepository extends CrudRepository<UrlEntity, UUID> {

    Optional<UrlEntity> findByCode(String code);

    Optional<UrlEntity> findByUrl(URL url);

    <S extends UrlEntity> S saveIfUnique(S urlEntity) throws NonUniqueCodeException;

}
