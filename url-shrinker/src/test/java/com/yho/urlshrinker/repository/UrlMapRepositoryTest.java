package com.yho.urlshrinker.repository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.yho.urlshrinker.entity.UrlEntity;
import com.yho.urlshrinker.exception.NonUniqueCodeException;
import com.yho.urlshrinker.mocks.UrlMocks;

public class UrlMapRepositoryTest {


    UrlMapRepository repository;

    UrlEntity defaultEntity;
    UUID defaultId = UUID.randomUUID();

    @BeforeEach
    void beforeEach() throws MalformedURLException{
        repository = new UrlMapRepository();
        defaultEntity = UrlMocks.mockEntity(defaultId, "defaultCode", "https://www.baeldung.com/parameterized-tests-junit-5");
    }
    

    @Test
    void count_emptyRepo_givesZero() {
        Assertions.assertEquals(0, repository.count());
    }

    @Test
    void count_nonEmptyRepo_givesAccurateResult() {
        //given
        repository.save(defaultEntity);

        // when / then
        Assertions.assertEquals(1, repository.count());
    }

    @Test
    void delete_existing_deletesIt() {
        //given
        repository.save(defaultEntity);

        // when
        repository.delete(defaultEntity);

        //  then
        Assertions.assertEquals(0, repository.count());
    }

    @Test
    void delete_nonExisting_doNothing() {
        //given
        repository.save(defaultEntity);

        // when
        repository.delete(UrlMocks.mockEntity());

        //  then
        Assertions.assertEquals(1, repository.count());
    }

    @Test
    void deleteAll_nonEmptyRepo_clearsIt() {
        // given
        var secondEntity = UrlMocks.mockEntity( "https://www.baeldung.com/spring-null-safety-annotations");
        repository.save(defaultEntity);
        repository.save(secondEntity);

        // when
        repository.deleteAll();

        // then
        Assertions.assertEquals(0, repository.count());
    }

    @Test
    void deleteAll_emptyRepo_doNothing() {
        // when
        repository.deleteAll();

        // then
        Assertions.assertEquals(0, repository.count());
    }

    @Test
    void deleteAllList_existing_deletesThem() {
        // given
        var second = UrlMocks.mockEntity();
        var third = UrlMocks.mockEntity();
        repository.saveAll(List.of(defaultEntity, second, third));

        // when
        repository.deleteAll(List.of(second, third));

        // then
        Assertions.assertEquals(1, repository.count());
    }

    @Test
    void deleteAllList_nonExisting_doNothing() {
        // given
        var second = UrlMocks.mockEntity();
        var third = UrlMocks.mockEntity();
        repository.saveAll(List.of(defaultEntity, second, third));

        // when
        repository.deleteAll(List.of(UrlMocks.mockEntity(), UrlMocks.mockEntity()));

        // then
        Assertions.assertEquals(3, repository.count());
    }


    void deleteAllById_existing_deletesThem() {
        // given
        var second = UrlMocks.mockEntity();
        var third = UrlMocks.mockEntity();
        repository.saveAll(List.of(defaultEntity, second, third));

        // when
        repository.deleteAllById(List.of(second.getId(), third.getId()));

        // then
        Assertions.assertEquals(1, repository.count());
    }

    @Test
    void deleteAllById_nonExisting_doNothing() {
        // given
        var second = UrlMocks.mockEntity();
        var third = UrlMocks.mockEntity();
        repository.saveAll(List.of(defaultEntity, second, third));

        // when
        repository.deleteAllById(List.of(UUID.randomUUID(), UUID.randomUUID()));

        // then
        Assertions.assertEquals(3, repository.count());
    }

    @Test
    void deleteById_existing_deletesIt() {
        //given
        repository.save(defaultEntity);

        // when
        repository.deleteById(defaultEntity.getId());

        //  then
        Assertions.assertEquals(0, repository.count());
    }

    @Test
    void deleteById_nonExisting_doNothing() {
        //given
        repository.save(defaultEntity);

        // when
        repository.deleteById(UUID.randomUUID());

        //  then
        Assertions.assertEquals(1, repository.count());
    }

    @Test
    void existsById_existing_givesTrue() {
        //given
        repository.save(defaultEntity);

        // when
        var result = repository.existsById(defaultId);

        //  then
        Assertions.assertTrue(result);
   }

    @Test
    void existsById_nonExisting_givesFalse() {
        //given
        repository.save(defaultEntity);

        // when
        var result = repository.existsById(UUID.randomUUID());

        //  then
        Assertions.assertFalse(result);
    }

    @Test
    void findAll_emptyRepo_givesEmptyList() {
        Assertions.assertFalse(repository.findAll().iterator().hasNext());
    }


    @Test
    void findAll_nonEmptyRepo_givesAll() {
         // given
        var data = List.of(defaultEntity, UrlMocks.mockEntity(), UrlMocks.mockEntity());
        repository.saveAll(data);

        // when
        var result = repository.findAll();

        // then
        var resultAsList = IterableUtil.toCollection(result);
        Assertions.assertEquals(3, resultAsList.size());
        data.forEach(it -> Assertions.assertTrue(resultAsList.contains(it)));
    }


    @Test
    void findAllById_emptyRepo_givesEmpty() {
        Assertions.assertFalse(repository.findAllById(List.of(UUID.randomUUID())).iterator().hasNext());
    }


    @Test
    void findAll_nonEmptyRepo_givesResults() {
        // given
        var second = UrlMocks.mockEntity();
        var third = UrlMocks.mockEntity();
        var data = List.of(defaultEntity, second, third);
        repository.saveAll(data);

        // when
        var result = repository.findAllById(List.of(second.getId(), defaultId));

        // then
        var resultAsList = IterableUtil.toCollection(result);
        Assertions.assertEquals(2, resultAsList.size());
        List.of(second, defaultEntity).forEach(it -> Assertions.assertTrue(resultAsList.contains(it)));
    }

    @Test
    void findAll_notExistingIdsNonEmptyRepo_gifindAllById_emptyRepo_givesEmptyvesResults() {
        // given
        repository.save(defaultEntity);

        // when / then
        Assertions.assertFalse(repository.findAllById(List.of(UUID.randomUUID())).iterator().hasNext());
    }

    @Test
    void findByCode_nonExistingCode_givesEmpty() {
        // given
        repository.save(defaultEntity);

        // when 
        var result = repository.findByCode("unknown");

        // then
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findByCode_existingCode_givesResult() {
        // given
        repository.save(defaultEntity);
        repository.save(UrlMocks.mockEntity());

        // when 
        var result = repository.findByCode(defaultEntity.getCode());

        // then
        Assertions.assertEquals(defaultEntity, result.get());
    }

    @Test
    void findById_nonExistingId_givesEmpty() {
        // given
        repository.save(defaultEntity);

        // when 
        var result = repository.findById(UUID.randomUUID());

        // then
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findById_existingId_givesResult() {
        // given
        repository.save(defaultEntity);
        repository.save(UrlMocks.mockEntity());

        // when 
        var result = repository.findById(defaultEntity.getId());

        // then
        Assertions.assertEquals(defaultEntity, result.get());
    }


    @Test
    void findByUrl_nonExistingId_givesEmpty() throws MalformedURLException {
        // given
        repository.save(defaultEntity);

        // when 
        var result = repository.findByUrl(new URL("http://www.google.com"));

        // then
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findByUrl_existingId_givesResult() {
        // given
        repository.save(defaultEntity);
        repository.save(UrlMocks.mockEntity());

        // when 
        var result = repository.findByUrl(defaultEntity.getUrl());

        // then
        Assertions.assertEquals(defaultEntity, result.get());
    }

    @Test
    void save_nonExistingData_storesAndReturnIt() {
        // given
        repository.save(UrlMocks.mockEntity());

        // when
        var result = repository.save(defaultEntity);

        // then 
        Assertions.assertEquals(2, repository.count());
        Assertions.assertEquals(result, defaultEntity);
        Assertions.assertEquals(result, defaultEntity);
        Assertions.assertEquals(result, repository.findById(defaultId).get());
    }

    @Test
    void save_existingData_updatesAndReturnIt() {
        // given
        repository.save(defaultEntity);
        
        // when
        var updated = UrlMocks.mockEntity(defaultId, "newCode", "http://newurl.com");
        var result = repository.save(updated);

        // then 
        Assertions.assertEquals(1, repository.count());
        Assertions.assertEquals(result, updated);
        Assertions.assertEquals(result, updated);
        Assertions.assertEquals(result, repository.findById(defaultId).get());
    }


    @Test
    void saveAll_notExisting_storesThem() {
        // given
        repository.save(UrlMocks.mockEntity());
        var second = UrlMocks.mockEntity();
        var third = UrlMocks.mockEntity();
        var data = List.of(defaultEntity, second, third);

        // when
        var result = repository.saveAll(data);

        // then
        Assertions.assertEquals(4, repository.count());
        var resultAsList = IterableUtil.toCollection(result);
        Assertions.assertEquals(3, resultAsList.size());
        List.of(second, defaultEntity).forEach(it -> Assertions.assertTrue(resultAsList.contains(it)));
    }

    @Test
    void saveAll_existing_storesThem() {
        // given
        repository.save(UrlMocks.mockEntity());
        repository.save(defaultEntity);
        var second = UrlMocks.mockEntity();
        var third = UrlMocks.mockEntity();
       
        // when
        var updated = UrlMocks.mockEntity(defaultId, "newCode", "http://newurl.com");
        var data = List.of(updated, second, third);
         var result = repository.saveAll(data);

        // then
        Assertions.assertEquals(4, repository.count());
        var resultAsList = IterableUtil.toCollection(result);
        Assertions.assertEquals(3, resultAsList.size());
        data.forEach(it -> Assertions.assertTrue(resultAsList.contains(it)));
    }

    @Test
    void saveIfCodeIsUnique_uniqueCode_storesAndReturnIt() {
        // given
        repository.save(UrlMocks.mockEntity());

        // when
        var result = repository.saveIfCodeIsUnique(defaultEntity);

        // then 
        Assertions.assertEquals(2, repository.count());
        Assertions.assertEquals(defaultEntity, defaultEntity);
        Assertions.assertEquals(result, defaultEntity);
        Assertions.assertEquals(result, repository.findById(defaultId).get());
    }

    @Test
    void saveIfCodeIsUnique_nonUniqueCode_throwsException() {
        // given
        repository.save(defaultEntity);
        
        // when
        var newWithSameCode = UrlMocks.mockEntity(UUID.randomUUID(), defaultEntity.getCode(), "http://newurl.com");
        NonUniqueCodeException e = Assertions.assertThrows(NonUniqueCodeException.class, () -> repository.saveIfCodeIsUnique(newWithSameCode));

        // then 
        Assertions.assertEquals(
            "500 INTERNAL_SERVER_ERROR, ProblemDetail[type='http://urlshrinker.com/errors/no-short-code', title='No short code available.', " + 
            "status=500, detail='Code already used', instance='null', properties='{Api-Error-Code=URL-0001}']", 
            e.getMessage());
        Assertions.assertEquals(newWithSameCode.getCode(), e.getCode());
    }

}
