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
// NDC: CrudRepository et non JPA ?
//  j'ai commencé avec l'idée de tester une approche sprinf-data-redis et ne voulais pas trop binder la persistence à JPA
//  finalement je suis revenu sur de la BD classique le CrudRespoitory est resté
//  il pourrait être remplacé par un repo plus abituel dans une version definitive 
//  le principale inconvenient ici est de gerere des iterable plutot que des List (beaucoup moins pratique; pour les tests unitqires notamment)
// 
//  si on part sur un repository JPA et que l'API demande unne recherche multicritere on 
//  remplacerait probablement les methode findByCode et findByUrl par une Specification
public interface UrlRepository extends CrudRepository<UrlEntity, UUID> {

    Optional<UrlEntity> findByCode(String code);

    Optional<UrlEntity> findByUrl(URL url);

    <S extends UrlEntity> S saveIfCodeIsUnique(S urlEntity) throws NonUniqueCodeException;

}
