package com.beautybuddy.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

public class JwtUtil {

    private static final long EXPIRATION_MS = 24L * 60 * 60 * 1000;

    private static SecretKey getSigningKey() {
        String secretB64 = System.getenv("JWT_SECRET_KEY");
        if (secretB64 == null || secretB64.isBlank()) {
            secretB64 = System.getProperty("JWT_SECRET_KEY");
        }
        if (secretB64 == null || secretB64.isBlank()) {
            throw new IllegalStateException("JWT_SECRET_KEY is missing (env var or JVM system property)");
        }

        byte[] keyBytes = Decoders.BASE64.decode(secretB64);

        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET_KEY is too short; need at least 32 bytes (base64-encoded)");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String generateToken(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(EXPIRATION_MS)))
                .signWith(getSigningKey())
                .compact();
    }

    public static String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public static boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
