package com.yho.urlshrinker.shrinker;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashShrinker implements UrlShrinker {

    @Override
    public String shrink(URL original) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("short code cant be generated", e);
        }
        byte[] encodedhash = digest.digest(
            original.toString().getBytes(StandardCharsets.UTF_8));
        return new String(encodedhash);
    }

    

}
