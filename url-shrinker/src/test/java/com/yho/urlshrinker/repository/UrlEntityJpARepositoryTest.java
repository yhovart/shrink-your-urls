package com.yho.urlshrinker.repository;

import java.net.URL;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import com.yho.urlshrinker.exception.NonUniqueCodeException;
import com.yho.urlshrinker.mocks.UrlMocks;

import jakarta.validation.ConstraintViolationException;



public class UrlEntityJpARepositoryTest extends AbstractJpaDbTest {

    @Autowired 
    UrlEntityJpARepository repository;


    // basic test to validate the test config
    @Test
    void findAll_afterSavingNewLonelyEntity_returnsIt() {
        // given
        var entity = UrlMocks.mockEntity("http://localhost:8080/url-shrinker");
        var saved = repository.saveAndFlush(entity);

        // when 
        var all = repository.findAll();

        // then
        var iterator = all.iterator();
        Assertions.assertEquals(saved, iterator.next());
        Assertions.assertFalse(iterator.hasNext());
    }

    @Test
    void saveIfCodeIsUnique_uniqueCode_savesAndReturnsIt() {
        // given
        var entity = UrlMocks.mockEntity("http://localhost:8080/url-shrinker");

        // when 
        var saved = repository.saveIfCodeIsUnique(entity);

        // then
        Assertions.assertEquals(1, repository.count());
        Assertions.assertEquals(saved.getCode(), entity.getCode());
        Assertions.assertEquals(saved.getUrl(), entity.getUrl());
        // Notice that JPA always generate a new id there
        // Assertions.assertEquals(saved.getId(), entity.getId());
        Assertions.assertNotNull(saved.getCreationDate());
        Assertions.assertNotNull(saved.getModifiedBy());
        Assertions.assertNotNull(saved.getCreator());
        Assertions.assertNotNull(saved.getCreationDate());
    }

    @Test
    void saveIfCodeIsUnique_nonUniqueCode_throwsNonUniqueException() {
        // given
        var firstOne = UrlMocks.mockEntity(UUID.randomUUID(), "old-code!", "http://old-url");
        var newOne = UrlMocks.mockEntity(UUID.randomUUID(), "old-code!", "http://new-url");
        repository.saveAndFlush(firstOne);

        // when 
        NonUniqueCodeException e = Assertions.assertThrows(NonUniqueCodeException.class, () -> repository.saveIfCodeIsUnique(newOne));

        // then
        Assertions.assertEquals(
            "500 INTERNAL_SERVER_ERROR, ProblemDetail[type='http://urlshrinker.com/errors/no-short-code', title='No short code available.', status=500, detail='Code already used', instance='null', properties='{Api-Error-Code=URL-0001}']", 
            e.getMessage());
    }


    @Test
    void saveIfCodeIsUnique_otherValidationException_preserveException() {
        // NDC: le but de ce test etait de tester la branche du du if dans le traitement de l'exception visant
        // a verifier si on est bien sur une exception liée a la contrainte d'unicité
        // malheureusement pas trouvé de cas pour generer le meme type d'exception; a creuser ou utiliser une approche par mock
        // de toutes facons la maniere de determiner s'il s'agit d'une unique exception est a revoir dans la classe elle meme
        
        // given
        var entity = UrlMocks.mockEntity(UUID.randomUUID(), "", "http://www.google.com");

        // when / then
        Assertions.assertThrows(ConstraintViolationException.class, () -> repository.saveIfCodeIsUnique(entity));
    }

}
