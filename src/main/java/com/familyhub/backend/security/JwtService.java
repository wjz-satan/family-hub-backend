package com.familyhub.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey key;
    private final Duration accessTokenTtl;
    private final Duration refreshTokenTtl;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access-token-ttl}") Duration accessTokenTtl,
                      @Value("${app.jwt.refresh-token-ttl}") Duration refreshTokenTtl) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenTtl = accessTokenTtl;
        this.refreshTokenTtl = refreshTokenTtl;
    }

    public String generateAccessToken(AuthenticatedUser user) {
        return generateToken(user, accessTokenTtl, "access");
    }

    public String generateRefreshToken(AuthenticatedUser user) {
        return generateToken(user, refreshTokenTtl, "refresh");
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(parse(token).get("tokenType", String.class));
    }

    public long getAccessTokenTtlSeconds() {
        return accessTokenTtl.toSeconds();
    }

    private String generateToken(AuthenticatedUser user, Duration ttl, String tokenType) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.userId()))
                .claims(Map.of(
                        "phone", user.phone(),
                        "nickname", user.nickname(),
                        "tokenType", tokenType
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttl)))
                .signWith(key)
                .compact();
    }
}
