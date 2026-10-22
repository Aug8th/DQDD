package org.example.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.Set;

@Service
public class GoogleTokenService {
    private final String clientId;
    private final JwtDecoder decoder;

    @Autowired
    public GoogleTokenService(@Value("${app.google.client-id:${GOOGLE_CLIENT_ID:}}") String clientId) {
        this(clientId, NimbusJwtDecoder
                .withJwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .jwsAlgorithm(SignatureAlgorithm.RS256).build());
    }

    GoogleTokenService(String clientId, JwtDecoder decoder) {
        this.clientId = clientId.trim();
        this.decoder = decoder;
    }

    public String clientId() { return clientId; }

    public Identity verify(String credential) {
        if (clientId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Google sign-in is not configured");
        }
        if (credential == null || credential.isBlank() || credential.length() > 20000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Google credential is required");
        }
        try {
            Jwt token = decoder.decode(credential);
            if (!validClaims(token)) throw new BadJwtException("Invalid Google identity claims");
            return new Identity(token.getSubject(), token.getClaimAsString("email"));
        } catch (JwtException | IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google credential is invalid or expired");
        }
    }

    private boolean validClaims(Jwt token) {
        String issuer = token.getClaimAsString("iss");
        String email = token.getClaimAsString("email");
        return Set.of("https://accounts.google.com", "accounts.google.com").contains(issuer == null ? "" : issuer)
                && token.getAudience().contains(clientId)
                && token.getExpiresAt() != null && token.getExpiresAt().isAfter(Instant.now())
                && token.getSubject() != null && !token.getSubject().isBlank()
                && token.getSubject().length() <= 255
                && Boolean.TRUE.equals(token.getClaimAsBoolean("email_verified"))
                && email != null && !email.isBlank() && email.length() <= 255;
    }

    public record Identity(String subject, String email) {}
}
