package com.yho.urlshrinker.repository;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;

import com.yho.urlshrinker.entity.UrlEntity;
import com.yho.urlshrinker.exception.NonUniqueCodeException;

import org.hibernate.exception.ConstraintViolationException;

@Profile("jpa")
public interface UrlEntiryJpARepository extends JpaRepository<UrlEntity, UUID>, UrlRepository {

  
    default <S extends UrlEntity> S saveIfUnique(S entity) throws NonUniqueCodeException {
        try{
            return this.saveAndFlush(entity);
        }catch(DataIntegrityViolationException e){
            // TODO faire plus fiable / beau
            if (e.getCause() instanceof ConstraintViolationException) {
                var cause = ((ConstraintViolationException)e.getCause()).getCause();
                if (cause != null && cause.getMessage().toLowerCase().contains("unique")){
                        throw new NonUniqueCodeException(entity.getCode(), "Code already used", e);  
                }
            }
            throw e;
        }
    };

}
