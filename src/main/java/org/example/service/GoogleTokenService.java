package org.example.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleTokenService {

    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String clientId;

    public String clientId() {
        return clientId;
    }

    public GoogleIdentity verify(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                return new GoogleIdentity(payload.getSubject(), payload.getEmail());
            }
        } catch (Exception e) {
            throw new RuntimeException("Xác thực Google Token thất bại: " + e.getMessage());
        }
        throw new RuntimeException("Google ID Token không hợp lệ!");
    }

    public record GoogleIdentity(String subject, String email) {}
}