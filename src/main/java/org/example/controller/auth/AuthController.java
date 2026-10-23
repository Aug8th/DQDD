package org.example.controller.auth;

import org.example.dto.response.UserResponse;
import org.example.entity.common.User;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.example.security.JwtUtils;
import org.example.service.auth.GoogleTokenService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final DaoAuthenticationProvider provider;
    private final JwtUtils jwt;
    private final GoogleTokenService google;

    public AuthController(UserRepository users, PasswordEncoder passwords,
                          DaoAuthenticationProvider provider, JwtUtils jwt, GoogleTokenService google) {
        this.users = users;
        this.passwords = passwords;
        this.provider = provider;
        this.jwt = jwt;
        this.google = google;
    }

    @GetMapping("/config")
    public Map<String, String> config() { return Map.of("googleClientId", google.clientId()); }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody Credentials body) {
        String email = email(body == null ? null : body.email());
        if (body.password() == null || body.password().isEmpty()
                || body.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        try {
            provider.authenticate(new UsernamePasswordAuthenticationToken(email, body.password()));
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email/password or account is disabled");
        }
        return response(users.findByEmail(email).orElseThrow());
    }

    @PostMapping("/google")
    public AuthResponse google(@RequestBody GoogleRequest body) {
        var identity = google.verify(body == null ? null : body.credential());
        var existing = users.findByGoogleSub(identity.subject());
        if (existing.isPresent()) {
            var user = existing.get();
            if (!"google".equals(user.getAuthProvider())) throw conflict();
            return response(user);
        }
        String email = email(identity.email());
        if (users.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This email already has an account. Sign in using its existing method; automatic Google linking is disabled");
        }
        var user = new User();
        user.setEmail(email);
        user.setGoogleSub(identity.subject());
        user.setAuthProvider("google");
        user.setRole("user");
        user.setActive(true);
        return response(save(user));
    }

    private User save(User user) {
        try { return users.saveAndFlush(user); }
        catch (DataIntegrityViolationException exception) { throw conflict(); }
    }

    private AuthResponse response(User user) {
        if (!user.isActive()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is disabled");
        return new AuthResponse(jwt.generateForEmail(user.getEmail()), "Bearer", UserMapper.toResponse(user));
    }

    private String email(String value) {
        if (value == null || value.trim().length() > 255 || !EMAIL.matcher(value.trim()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid email is required");
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private ResponseStatusException conflict() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Email or Google account already exists");
    }

    public record Credentials(String email, String password) {}
    public record GoogleRequest(String credential) {}
    public record AuthResponse(String accessToken, String tokenType, UserResponse user) {
        @com.fasterxml.jackson.annotation.JsonProperty("token")
        public String token() { return accessToken; }
        @com.fasterxml.jackson.annotation.JsonProperty("id")
        public Integer id() { return user.id(); }
        @com.fasterxml.jackson.annotation.JsonProperty("email")
        public String email() { return user.email(); }
    }
}
