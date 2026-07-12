package com.example.Proyecto.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationSeconds;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-seconds:3600}") long expirationSeconds) {
        if (secret.length() < 32) throw new IllegalStateException("app.jwt.secret debe tener mínimo 32 caracteres");
        this.key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.expirationSeconds = expirationSeconds;
    }

    public String generate(UserDetails user) {
        Instant now = Instant.now();
        String role = user.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("ROLE_CLIENTE");
        return Jwts.builder().subject(user.getUsername()).claim("rol", role)
            .issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(expirationSeconds)))
            .signWith(key).compact();
    }

    public String username(String token) { return claims(token).getSubject(); }
    public boolean isValid(String token, UserDetails user) {
        return user.getUsername().equalsIgnoreCase(username(token)) && claims(token).getExpiration().after(new Date());
    }
    private Claims claims(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); }
}
