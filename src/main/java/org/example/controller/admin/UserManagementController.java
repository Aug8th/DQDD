package org.example.controller.admin;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
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
@RequestMapping("/api/admin/users")
public class UserManagementController {
    private final UserRepository users;
    private final PasswordEncoder passwords;
    public UserManagementController(UserRepository users, PasswordEncoder passwords) { this.users = users; this.passwords = passwords; }
    public record UserRequest(String email, String password, Boolean active, String avatarUrl) {}
    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Integer id) { return UserMapper.toResponse(find(id)); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public UserResponse create(@RequestBody UserRequest request) {
        User user = new User(); user.setRole("admin"); user.setAuthProvider("local");
        return save(user, request, true);
    }
    @PutMapping("/{id}")
    @Transactional
    public UserResponse update(Authentication auth, @PathVariable Integer id, @RequestBody UserRequest request) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Update your own profile through the account settings page");
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(Authentication auth, @PathVariable Integer id) { throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Deleting accounts is disabled"); }
    private User find(Integer id) {
        return users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
    private UserResponse save(User user, UserRequest request, boolean creating) {
        if (request == null || request.email() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (email.length() > 255 || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email");
        var existing = users.findByEmail(email);
        if (existing.isPresent() && !existing.get().getId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        String password = request.password();
        if ("google".equals(user.getAuthProvider()) && (!email.equals(user.getEmail()) || (password != null && !password.isEmpty())))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Google email and password cannot be changed here");
        if (creating || (password != null && !password.isEmpty())) {
            if (password == null || password.length() < 8 || password.getBytes(StandardCharsets.UTF_8).length > 72)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must contain at least 8 characters and at most 72 UTF-8 bytes");
            user.setPasswordHash(passwords.encode(password));
        }
        if (request.avatarUrl() != null) {
            String avatar = request.avatarUrl().trim();
            if (avatar.length() > 500 || (!avatar.isEmpty() && !avatar.startsWith("/uploads/") && !avatar.matches("https?://[^\\s]+")))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid avatar URL (HTTP, HTTPS or /uploads/, max 500)");
            user.setAvatarUrl(avatar.isEmpty() ? null : avatar);
        }
        user.setEmail(email);
        user.setActive(true);
        return UserMapper.toResponse(users.saveAndFlush(user));
    }
}
