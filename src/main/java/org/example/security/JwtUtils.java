package org.example.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtils {
    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtUtils(@Value("${app.jwt.secret:${JWT_SECRET:}}") String secret,
                    @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
        byte[] bytes;
        if (secret == null || secret.isBlank()) {
            bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);
        } else {
            bytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        if (bytes.length < 32 || expirationMs <= 0) {
            throw new IllegalArgumentException("JWT secret must have at least 32 UTF-8 bytes and expiration must be positive");
        }
        this.signingKey = Keys.hmacShaKeyFor(bytes);
        this.expirationMs = expirationMs;
    }

    public String generateJwtToken(UserDetails userDetails) {
        return generateForEmail(userDetails.getUsername());
    }

    public String generateForEmail(String email) {
        Instant now = Instant.now();
        return Jwts.builder().setIssuer("mealwheel").setSubject(email)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(signingKey, SignatureAlgorithm.HS256).compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().requireIssuer("mealwheel")
                .setSigningKey(signingKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            String subject = getUserNameFromJwtToken(token);
            return subject != null && !subject.isBlank();
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }
}
