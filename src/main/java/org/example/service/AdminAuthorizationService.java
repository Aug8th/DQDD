package org.example.service;

import org.example.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@Service
public class AdminAuthorizationService {
    private final UserRepository users;
    public AdminAuthorizationService(UserRepository users) {
        this.users = users;
    }
    public boolean isAdminOwner(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return false;
        }
        return users.findByEmail(authentication.getName())
                .filter(user -> "admin".equals(user.getRole()) && user.isAdminOwner())
                .isPresent();
    }
    public void requireAdminOwner(Authentication authentication) {
        if (!isAdminOwner(authentication)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the admin owner can manage admin accounts"
            );
        }
    }
}