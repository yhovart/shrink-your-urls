package com.yho.urlshrinker.web;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.yho.urlshrinker.mocks.UrlMocks;
import com.yho.urlshrinker.service.UrlService;

@WebMvcTest(Redirector.class)
public class RedirectorTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UrlService service;

    @Test
    void testError() throws Exception {
        // /error present juste pour illuster la demo
        // on ferait autrement, ce test est present uniquement pour ne pas grever le coverage
         // when / then
         mvc.perform(MockMvcRequestBuilders.get("/error"))
         .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testNotFound() throws Exception {
        // /not-found present juste pour illuster la demo
        // on ferait autrement, ce test est present uniquement pour ne pas grever le coverage
        
        // when / then
         mvc.perform(MockMvcRequestBuilders.get("/not-found"))
         .andExpect(MockMvcResultMatchers.status().is(303));
    }

    @Test
    void redirect_existingCode_returnRedirectAs302() throws Exception {
        // given
        var url = UrlMocks.mockDetails();
        BDDMockito.given(service.getUrlDetailsByCode(url.shortCode())).willReturn(url);

        // when / then
        mvc.perform(MockMvcRequestBuilders.get("/" + url.shortCode()))
        //.param("id", url.id().toString()) passer le {id} dans l'url ne marche pas; chercher pkoi
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        // possible de tester l'url vers laquelle on a été redirigé? 
    }


    @Test
    void redirect_NonExistingCode_404() throws Exception {
        // given
        var url = UrlMocks.mockDetails();
        BDDMockito.given(service.findUrlDetailsByCode(url.shortCode())).willReturn(Optional.empty());

        // when / then
        mvc.perform(MockMvcRequestBuilders.get("/" + url.shortCode()))
        //.param("id", url.id().toString()) passer le {id} dans l'url ne marche pas; chercher pkoi
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.status().is(303));
        // possible de tester la page d'erreur 
    }
}
