package org.example.service;

import org.springframework.stereotype.Service;

@Service("legacyGoogleTokenService")
public class GoogleTokenService {
    private final org.example.service.auth.GoogleTokenService delegate;

    public GoogleTokenService(org.example.service.auth.GoogleTokenService delegate) {
        this.delegate = delegate;
    }

    public String clientId() { return delegate.clientId(); }

    public GoogleIdentity verify(String credential) {
        var identity = delegate.verify(credential);
        return new GoogleIdentity(identity.subject(), identity.email());
    }

    public record GoogleIdentity(String subject, String email) {}
}
