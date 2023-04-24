package com.yho.urlshrinker.web;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yho.urlshrinker.domain.UrlDetails;
import com.yho.urlshrinker.mocks.UrlMocks;
import com.yho.urlshrinker.service.UrlService;

@WebMvcTest(UrlController.class)
public class UrlControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UrlService service;

    @Test
    void addUrl_notExisting_addThenReturnsCREATED() throws JsonProcessingException, Exception {
        // given
        var url = UrlMocks.mockDetails();
        BDDMockito.given(service.findUrlDetails(url.originalUrl())).willReturn(Optional.empty());
        BDDMockito.given(service.createUrl(url.originalUrl())).willReturn(url);

        // when / then
        var matcher = mvc.perform(MockMvcRequestBuilders.post("/api/v1/urls")
            //.param("id", url.id().toString()) passer le {id} dans l'url ne marche pas; chercher pkoi
            .content(asJson(url.originalUrl()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated());
        assertSame(matcher, url);
    }

    
    @Test
    void addUrl_existing_justReturnsAndOK() throws JsonProcessingException, Exception {
        // given
        var url = UrlMocks.mockDetails();
        BDDMockito.given(service.findUrlDetails(url.originalUrl())).willReturn(Optional.of(url));

        // when / then
        var matcher = mvc.perform(MockMvcRequestBuilders.post("/api/v1/urls")
            //.param("id", url.id().toString()) passer le {id} dans l'url ne marche pas; chercher pkoi
            .content(asJson(url.originalUrl()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
        assertSame(matcher, url);
    }

    @Test
    void testClear() throws Exception {
        // given
        BDDMockito.doNothing().when(service).clear();

        // when / then
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/urls"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void testGetUrl() throws Exception {
        // given
        var url = UrlMocks.mockDetails();
        BDDMockito.given(service.getUrl(url.id())).willReturn(url);

        // when / then
        var matcher = mvc.perform(MockMvcRequestBuilders.get("/api/v1/urls/" + url.id().toString())
        //.param("id", url.id().toString()) passer le {id} dans l'url ne marche pas; chercher pkoi
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
        assertSame(matcher, url);
    }

    @Test
    void testGetUrls() throws Exception {
        // given
        var url1 = UrlMocks.mockDetails();
        var url2 = UrlMocks.mockDetails();
        BDDMockito.given(service.getUrls()).willReturn(List.of(url1, url2));

        // when / then
        var matcher = mvc.perform(MockMvcRequestBuilders.get("/api/v1/urls/search")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
        assertSame(matcher, 0, url1);
        assertSame(matcher, 1, url2);
    }

    private ResultActions assertSame(ResultActions matcher, UrlDetails url) throws Exception{
        return assertSame(matcher, url, "$");
    }

    private ResultActions assertSame(ResultActions matcher, int index, UrlDetails url) throws Exception{
        return assertSame(matcher, url, "$[" + index + "]");
    }

    private ResultActions assertSame(ResultActions matcher, UrlDetails url, String jsonPrefix) throws Exception {
        return matcher.andExpect(MockMvcResultMatchers.jsonPath(jsonPrefix + ".id").value(url.id().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath(jsonPrefix + ".shortCode").value(url.shortCode()))
        .andExpect(MockMvcResultMatchers.jsonPath(jsonPrefix + ".originalUrl").value(url.originalUrl().toString()));
    }

    private String asJson(URL url) {
        return " { \"url\" : \"" + url + "\" }";
    }


}
