package com.yho.urlshrinker;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.yho.urlshrinker.domain.UrlDetails;
import com.yho.urlshrinker.service.UrlService;

import jakarta.servlet.http.HttpServletRequest;


/**
 * Redirects the client to the correspondong url.
 * This endpoint is not a part of the REST API that handles the Urls storage.
 * In real life they could be two separate applications; for instance if the API usage is limited to other applications or a controlled population (employees for instance).
 */
@Controller // Not a REST endpoint
@Validated //Unlike RestControllers, Contorllers are not validated by default
public class Redirector implements ErrorController {

    private final UrlService urlService;    
    
    public Redirector(UrlService urlService) {
        this.urlService = urlService;
    }


    @GetMapping("/{urlCode}")
    public RedirectView redirectClientToLongUrl(@PathVariable String urlCode) {
        return this.urlService.findUrlDetailsByCode(urlCode).map(
           this::toRedirectView
        ).orElse(this.notFound());
    }


    // basic error handling for the NON REST API porion of the app
    @RequestMapping("/error")
    @ResponseBody
    String error(HttpServletRequest request) {
        return "<h1>Error occurred</h1>";
    }

    @RequestMapping("/notFound")
    @ResponseBody
    String notFound(HttpServletRequest request) {
        return "<h1>404 - Not Found</h1>";
    }

    private RedirectView toRedirectView(UrlDetails urlDetais){
        return new RedirectView(urlDetais.originalUrl().toString(), false, false, false);
    }


    private RedirectView notFound(){
        return new RedirectView("/notFound", false, false, false);
    }


}
