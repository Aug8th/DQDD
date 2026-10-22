package org.example.controller.admin;

import java.nio.charset.StandardCharsets;
import org.example.dto.response.UserResponse;
import org.example.entity.common.User;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/account")
public class AdminProfileController {
    private final UserRepository users;
    private final PasswordEncoder passwords;
    public AdminProfileController(UserRepository users, PasswordEncoder passwords) { this.users = users; this.passwords = passwords; }
    public record PasswordRequest(String currentPassword, String newPassword) {}
    private User current(Authentication auth) {
        return users.findByEmail(auth.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }
    @GetMapping("/me")
    public UserResponse me(Authentication auth) { return UserMapper.toResponse(current(auth)); }
    @DeleteMapping("/avatar")
    @Transactional
    public UserResponse removeAvatar(Authentication auth) {
        User user = current(auth); user.setAvatarUrl(null); return UserMapper.toResponse(user);
    }
    @PostMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void password(Authentication auth, @RequestBody PasswordRequest request) {
        User user = current(auth);
        if (!"local".equals(user.getAuthProvider()) || user.getPasswordHash() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Google accounts manage their password with Google");
        if (request == null || request.currentPassword() == null || request.newPassword() == null
                || request.newPassword().length() < 8 || request.newPassword().getBytes(StandardCharsets.UTF_8).length > 72)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must have at least 8 characters and at most 72 UTF-8 bytes");
        if (request.currentPassword().getBytes(StandardCharsets.UTF_8).length > 72 || !passwords.matches(request.currentPassword(), user.getPasswordHash()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        user.setPasswordHash(passwords.encode(request.newPassword()));
    }
}
