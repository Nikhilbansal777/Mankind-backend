package com.mankind.corporateauthservice.service;

import com.mankind.corporateauthservice.model.CorporateUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final JwtParser jwtParser;
    private final long expiresInSeconds;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expires-in-seconds}") long expiresInSeconds
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parser()
                .verifyWith(signingKey)
                .build();
        this.expiresInSeconds = expiresInSeconds;
    }

    public String generateToken(CorporateUser user) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(expiresInSeconds);

        Map<String, Object> claims = new HashMap<>();
        claims.put("corporateName", user.getCorporateName());
        claims.put("dateOfJoining", user.getDateOfJoining().toString());

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claims(claims)
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            return jwtParser
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }
}
