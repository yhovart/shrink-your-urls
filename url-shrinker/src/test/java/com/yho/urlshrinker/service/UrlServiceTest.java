package com.yho.urlshrinker.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import com.yho.urlshrinker.domain.UrlDetails;
import com.yho.urlshrinker.entity.UrlEntity;
import com.yho.urlshrinker.exception.NonUniqueCodeException;
import com.yho.urlshrinker.exception.UrlCodeNotFound;
import com.yho.urlshrinker.exception.UrlDetailsNotFound;
import com.yho.urlshrinker.mocks.UrlMocks;
import com.yho.urlshrinker.repository.UrlRepository;
import com.yho.urlshrinker.shrinker.UrlShrinker;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = { "url-shrinker.retryOnNonUniqueCode=2", "url-shrinker.shortCodeLength=9" }) 
public class UrlServiceTest {


    // Mockito doesnt support injecting @Value
    // we dont want to make the class code clunkier to save a line so we'll instanciate the service ourself
    // @InjectMocks
    UrlService service;

    @Mock 
    UrlRepository urlRepository;

    @Mock 
    UrlShrinker urlShrinker;

    @BeforeEach
    public void beforeEach(){
        this.service = new UrlService(urlRepository, urlShrinker,3, 9);
    }


    @Test
    void testClear() {
        // given
        BDDMockito.doNothing().when(urlRepository).deleteAll();

        // when
        service.clear();

        // then
        BDDMockito.verify(urlRepository).deleteAll();

    }

    @Test
    void createURL_noCollision_createsAndReturn() throws MalformedURLException {
        // given
        var url = new URL("http://newurl");
        var shortCode = asBase64Code("shortCode", 9);
        BDDMockito.given(urlShrinker.shrink(url)).willReturn("shortCode");
        var entity = UrlMocks.mockEntity(UUID.randomUUID(), shortCode, url.toString()); //capturer l'entity passé lors de l'appel 
        BDDMockito.given(urlRepository.saveIfCodeIsUnique(argThat(it -> it.getCode().equals(shortCode) && it.getUrl().equals(url)))).willReturn(entity);

        // when
        var result = service.createUrl(entity.getUrl());

        // then
        assertSame(result, entity);
        Assertions.assertEquals(result.shortCode().length(), 9);
    }


    @Test
    void createURL_noCollisionShorterCode_createsAndReturn() throws MalformedURLException {
        // given
        service = new UrlService(urlRepository, urlShrinker, 3, 2);
        var url = new URL("http://newurl");
        var shortCode = asBase64Code("shortCode", 2);
        BDDMockito.given(urlShrinker.shrink(url)).willReturn("shortCode");
        var entity = UrlMocks.mockEntity(UUID.randomUUID(), shortCode, url.toString()); //capturer l'entity passé lors de l'appel 
        BDDMockito.given(urlRepository.saveIfCodeIsUnique(argThat(it -> it.getCode().equals(shortCode) && it.getUrl().equals(url)))).willReturn(entity);

        // when
        var result = service.createUrl(entity.getUrl());

        // then
        assertSame(result, entity);
        Assertions.assertEquals(result.shortCode().length(), 2);
    }


    @Test
    void createURL_twoCollisions_createsAndReturn() throws MalformedURLException {
        // given
        var url = new URL("http://newurl");
        var shortCode3 = asBase64Code("shortCode3", 9);
        BDDMockito.given(urlShrinker.shrink(url)).willReturn("shortCode1", "shortCode2", "shortCode3");

        var entity = UrlMocks.mockEntity(UUID.randomUUID(), shortCode3, url.toString());         
        BDDMockito.given(urlRepository.saveIfCodeIsUnique(argThat(it -> it.getUrl().equals(url))))
            .willThrow(new NonUniqueCodeException("shortCode1", "deja pris!"))
            .willThrow(new NonUniqueCodeException("shortCode2", "decidemment pas dbol!"))
            .willReturn(entity);

        // when
        var result = service.createUrl(entity.getUrl());

        // then
        assertSame(result, entity);
        Assertions.assertEquals(result.shortCode().length(), 9);

        BDDMockito.verify(urlShrinker, times(3)).shrink(url);
        BDDMockito.verify(urlRepository, times(3)).saveIfCodeIsUnique(any());
    }

    @Test
    void createURL_fourCollisions_throwsException() throws MalformedURLException {
        // given
        var url = new URL("http://newurl");
        var shortCode3 = asBase64Code("shortCode3", 9);
        BDDMockito.given(urlShrinker.shrink(url)).willReturn("shortCode1", "shortCode2", "shortCode3", "shortCode4");

        var entity = UrlMocks.mockEntity(UUID.randomUUID(), shortCode3, url.toString());         
        BDDMockito.given(urlRepository.saveIfCodeIsUnique(argThat(it -> it.getUrl().equals(url))))
            .willThrow(new NonUniqueCodeException("shortCode1", "deja pris!"))
            .willThrow(new NonUniqueCodeException("shortCode2", "decidemment pas dbol!"))
            .willThrow(new NonUniqueCodeException("shortCode3", "toujours pas"))
            .willThrow(new NonUniqueCodeException("shortCode4", "mais n'insistez pas!"))
            .willReturn(entity);

        // when
        NonUniqueCodeException e = Assertions.assertThrows(NonUniqueCodeException.class, () -> service.createUrl(entity.getUrl()));

        // then
        Assertions.assertEquals(
            "500 INTERNAL_SERVER_ERROR, ProblemDetail[type='http://urlshrinker.com/errors/no-short-code', title='No short code available.', " +
            "status=500, detail='mais n'insistez pas!', instance='null', properties='{Api-Error-Code=URL-0001}']",
            e.getMessage());
    }

    @Test
    void createURL_oneCollisionsThenOtherException_preserveException() throws MalformedURLException {
        // given
        var url = new URL("http://newurl");
        var shortCode3 = asBase64Code("shortCode3", 9);
        BDDMockito.given(urlShrinker.shrink(url)).willReturn("shortCode1", "shortCode2", "shortCode3");

        var entity = UrlMocks.mockEntity(UUID.randomUUID(), shortCode3, url.toString());         
        BDDMockito.given(urlRepository.saveIfCodeIsUnique(argThat(it -> it.getUrl().equals(url))))
            .willThrow(new NonUniqueCodeException("shortCode1", "deja pris!"))
            .willThrow(new RuntimeException("Autre erreur quelconque"))
            .willReturn(entity);

        // when
        RuntimeException e = Assertions.assertThrows(RuntimeException.class, () -> service.createUrl(entity.getUrl()));

        // then
        Assertions.assertEquals("Autre erreur quelconque", e.getMessage());
    }

    @Test
    void createURL_twoCollisionZeroRetry_createsAndReturn() throws MalformedURLException {
        // given
        service = new UrlService(urlRepository, urlShrinker, 0, 9);
        var url = new URL("http://newurl");
        var shortCode3 = asBase64Code("shortCode3", 9);
        BDDMockito.given(urlShrinker.shrink(url)).willReturn("shortCode1", "shortCode2", "shortCode3");

        var entity = UrlMocks.mockEntity(UUID.randomUUID(), shortCode3, url.toString());         
        BDDMockito.given(urlRepository.saveIfCodeIsUnique(argThat(it -> it.getUrl().equals(url))))
            .willThrow(new NonUniqueCodeException("shortCode1", "deja pris!"));


        // when
        NonUniqueCodeException e = Assertions.assertThrows(NonUniqueCodeException.class, () -> service.createUrl(entity.getUrl()));

        // then
        Assertions.assertEquals(
            "500 INTERNAL_SERVER_ERROR, ProblemDetail[type='http://urlshrinker.com/errors/no-short-code', title='No short code available.', " +
            "status=500, detail='deja pris!', instance='null', properties='{Api-Error-Code=URL-0001}']",
            e.getMessage());
    }

    @Test
    void testFindUrlDetails() {
        // given
        var entity = UrlMocks.mockEntity();
        BDDMockito.given(urlRepository.findByUrl(entity.getUrl())).willReturn(Optional.of(entity));

        // when
        var result = service.findUrlDetails(entity.getUrl());


        // then
        assertSame(result, entity);
    }

    
    @Test
    void findUrlDetails_nonExistingEntity_returnsEmpty() throws MalformedURLException {
        // given
        var url = new URL("http://random-url");
        BDDMockito.given(urlRepository.findByUrl(url)).willReturn(Optional.empty());

        // when
        var result = service.findUrlDetails(url);

        // then
        Assertions.assertTrue(result.isEmpty());
    }


    @Test
    void findUrlDetails_existingEntity_returnsEquivalentResult() {
        // given
        var entity = UrlMocks.mockEntity();
        BDDMockito.given(urlRepository.findByCode(entity.getCode())).willReturn(Optional.of(entity));

        // when
        var result = service.findUrlDetailsByCode(entity.getCode());


        // then
        assertSame(result, entity);
    }

    @Test
    void findUrlDetailsByCode_nonExistingEntity_returnsEmpty() {
        // given
        var code = "random Code";
        BDDMockito.given(urlRepository.findByCode(code)).willReturn(Optional.empty());

        // when
        var result = service.findUrlDetailsByCode(code);

        // then
        Assertions.assertTrue(result.isEmpty());
    }


    @Test
    void getUrl_existingEntity_returnsEquivalentResult() {
        // given
        var entity = UrlMocks.mockEntity();
        BDDMockito.given(urlRepository.findById(entity.getId())).willReturn(Optional.of(entity));

        // when
        var result = service.getUrl(entity.getId());

        // then
        assertSame(result, entity);
    }


    @Test
    void getUrl_nonExistingEntity_throwsException() {
        // given
        var id = UUID.fromString("bfd72fe0-3149-4997-bf07-2fc5e208d5d9");
        BDDMockito.given(urlRepository.findById(id)).willReturn(Optional.empty());

        // when
        UrlDetailsNotFound e = Assertions.assertThrows(UrlDetailsNotFound.class, () -> service.getUrl(id));

        // then
        Assertions.assertEquals("Resource urls with id bfd72fe0-3149-4997-bf07-2fc5e208d5d9 not found.", e.getMessage());
        Assertions.assertEquals(id.toString(), e.getId());
    }


    @Test
    void getUrlDetailsByCode_existingEntity_returnsEquivalentResult() {
        // given
        var entity = UrlMocks.mockEntity();
        BDDMockito.given(urlRepository.findByCode(entity.getCode())).willReturn(Optional.of(entity));

        // when
        var result = service.getUrlDetailsByCode(entity.getCode());


        // then
        assertSame(result, entity);
    }

    @Test
    void getUrlDetailsByCode_nonExistingEntity_throwsException() {
        // given
        var code = "random Code";
        BDDMockito.given(urlRepository.findByCode(code)).willReturn(Optional.empty());

        // when
        UrlCodeNotFound e = Assertions.assertThrows(UrlCodeNotFound.class, () -> service.getUrlDetailsByCode(code));

        // then
        Assertions.assertEquals("Resource urls with id random Code not found.", e.getMessage());
        Assertions.assertEquals(code, e.getId());
    }

    @Test
    void getUrls_nonEmpty_returnsEquivalentResults() {
        // given
        var entities = List.of(UrlMocks.mockEntity(), UrlMocks.mockEntity());
        BDDMockito.given(urlRepository.findAll()).willReturn(entities);

        // when
        var result = service.getUrls();

        // then
        Assertions.assertEquals(result.size(), entities.size());
        for(int i = 0; i < result.size(); i++){ // pas trouvé d'approche lambda simple pour iterer les 2 listes sur le meme index
            assertSame(result.get(i), entities.get(i));
        }


    }


    private void assertSame(Optional<UrlDetails> urlDetails, UrlEntity entity) {
        if (entity == null){
            Assertions.assertTrue(urlDetails.isEmpty());
        }else {
            assertSame(urlDetails.get(), entity);
        }
    }

    private void assertSame(UrlDetails urlDetails, UrlEntity entity) {
        Assertions.assertEquals(urlDetails.shortCode(), entity.getCode());
        Assertions.assertEquals(urlDetails.id(), entity.getId());
        Assertions.assertEquals(urlDetails.originalUrl(), entity.getUrl());
    }


    private String asBase64Code(String shortCode, int shortCodeLength){
        // dupiliquer l'impem n'est pas tres elegant; on pourait deplacer l'encoreur dans un compsant a part
        var encoded = Base64.getUrlEncoder().encodeToString(shortCode.getBytes());
        if (encoded.length() > shortCodeLength){
            return encoded.substring(0, shortCodeLength);
        }
        return encoded;
    }

}
