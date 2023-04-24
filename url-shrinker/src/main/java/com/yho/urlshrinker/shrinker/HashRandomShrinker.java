package com.yho.urlshrinker.shrinker;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.lang.NonNull;

/**
 * Using a hash to generate the code.
 * As we want unique results we also use a timestamp before hashing.
 * Collisions are still possible if called during the same millisecond with the same url.
 */
// NDC : hashing but random --> using a hash is pointless ?
public class HashRandomShrinker implements UrlShrinker {

    @Override
    public String shrink(@NonNull URL url) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("short code cant be generated", e);
        }
        // we're not using hashing for reproductibility here; we want unique results each time
        var stringToHash = System.currentTimeMillis() + url.toString();
        var encodedhash = digest.digest(stringToHash.getBytes(StandardCharsets.UTF_8));
        return new String(encodedhash);
    }

    

}
