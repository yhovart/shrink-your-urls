package com.yho.urlshrinker.shrinker;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HashShrinkerTest {

    HashRandomShrinker shrinker = new HashRandomShrinker(); 

    @Test
    void shrinking_NonNullUrl_GivesAResult() throws MalformedURLException {
        // given
        var url = new URL("https://instagram-engineering.com/sharding-ids-at-instagram-1cf5a71e5a5c");

        // when
        var result = shrinker.shrink(url);

        //then
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isBlank());
    }


    @Test
    void shrinking_TwoUrls_GivesDifferentResults() throws MalformedURLException {
        // given
        var url1 = new URL("https://instagram-engineering.com/sharding-ids-at-instagram-1cf5a71e5a5c");
        var url2 = new URL("https://photutorial.com/photos-statistics/");

        // when
        var result1 = shrinker.shrink(url1);
        var result2 = shrinker.shrink(url2);

        //then
        Assertions.assertNotEquals(result1, result2);
    }

    @Test
    void shrinking_UrlTwice_GivesDifferentResults() throws MalformedURLException {
        // given
        var url = new URL("https://instagram-engineering.com/sharding-ids-at-instagram-1cf5a71e5a5c");

        // when
        var result1 = shrinker.shrink(url);
        try{ Thread.sleep(1); } catch (InterruptedException e) { }
        var result2 = shrinker.shrink(url);

        //then
        Assertions.assertNotEquals(result1, result2);
    }

}
