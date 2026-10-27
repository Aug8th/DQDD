package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.LoginRequest;
import org.example.dto.request.RegisterRequest;
import org.example.dto.response.AuthResponse;
import org.example.entity.common.User;
import org.example.repository.UserRepository;
import org.example.security.JwtUtils;
import org.example.service.notification.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final GoogleTokenService googleTokenService;
    private final EmailService emailService;

    @Transactional
    public String register(RegisterRequest request) {
        String email = normalizeEmail(request == null ? null : request.getEmail());
        validatePassword(request.getPassword());
        if (userRepository.existsByEmail(email)) {
            throw error(HttpStatus.CONFLICT, "Email already exists");
        }
        String otp = newOtp();
        var user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setAuthProvider("local");
        user.setRole("user");
        user.setActive(false);
        user.setResetOtp("REGISTER:" + otp);
        user.setOtpExpiryDate(LocalDateTime.now().plusMinutes(10));
        userRepository.saveAndFlush(user);
        emailService.sendAuthenticationCode(email, "Verify your MealWheel account",
                "Your registration code is " + otp + ". It expires in 10 minutes.");
        return "Check your email and verify the registration code before signing in";
    }

    @Transactional
    public String verifyAccount(String email, String otp) {
        User user = find(email);
        if (user.isActive()) throw error(HttpStatus.CONFLICT, "Account is already active");
        requireOtp(user, "REGISTER:", otp);
        user.setActive(true);
        clearOtp(user);
        userRepository.save(user);
        return "Account verified. You can now sign in";
    }

    public AuthResponse login(LoginRequest request) {
        User user = find(request.getEmail());
        requireLocal(user);
        if (!user.isActive() || request.getPassword() == null
                || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw error(HttpStatus.UNAUTHORIZED, "Invalid credentials or account is disabled");
        }
        return response(user);
    }

    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = find(email);
        requireLocal(user);
        validatePassword(newPassword);
        if (oldPassword == null || !passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw error(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        clearOtp(user);
        userRepository.save(user);
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = find(email);
        requireLocal(user);
        if (!user.isActive()) throw error(HttpStatus.FORBIDDEN, "Account is disabled or not verified");
        String otp = newOtp();
        user.setResetOtp("RESET:" + otp);
        user.setOtpExpiryDate(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        emailService.sendAuthenticationCode(user.getEmail(), "Reset your MealWheel password",
                "Your password reset code is " + otp + ". It expires in 15 minutes.");
    }

    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        User user = find(email);
        requireLocal(user);
        if (!user.isActive()) throw error(HttpStatus.FORBIDDEN, "Account is disabled or not verified");
        validatePassword(newPassword);
        requireOtp(user, "RESET:", otp);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        clearOtp(user);
        userRepository.save(user);
    }

    @Transactional
    public AuthResponse googleLogin(String credential) {
        var identity = googleTokenService.verify(credential);
        User user = userRepository.findByGoogleSub(identity.subject()).orElseGet(() -> {
            String email = normalizeEmail(identity.email());
            if (userRepository.existsByEmail(email)) {
                throw error(HttpStatus.CONFLICT, "Email already exists; use the original sign-in method");
            }
            var created = new User();
            created.setEmail(email);
            created.setGoogleSub(identity.subject());
            created.setAuthProvider("google");
            created.setRole("user");
            created.setActive(true);
            return userRepository.saveAndFlush(created);
        });
        if (!user.isActive() || !"google".equals(user.getAuthProvider())) {
            throw error(HttpStatus.FORBIDDEN, "Account is disabled or has a different sign-in method");
        }
        return response(user);
    }

    private AuthResponse response(User user) {
        return new AuthResponse(
                jwtUtils.generateForEmail(user.getEmail()),
                user.getId().longValue(),
                user.getEmail(),
                user.getRole(),
                user.isAdminOwner()
        );
    }

    private User find(String email) {
        return userRepository.findByEmail(normalizeEmail(email)).orElseThrow(() ->
                error(HttpStatus.BAD_REQUEST, "Account not found"));
    }

    private String normalizeEmail(String email) {
        if (email == null || email.trim().length() > 255 || !EMAIL.matcher(email.trim()).matches()) {
            throw error(HttpStatus.BAD_REQUEST, "A valid email is required");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw error(HttpStatus.BAD_REQUEST, "Password must contain at least 8 characters and at most 72 UTF-8 bytes");
        }
    }

    private void requireLocal(User user) {
        if (!"local".equals(user.getAuthProvider()) || user.getPasswordHash() == null) {
            throw error(HttpStatus.BAD_REQUEST, "Use your original Google sign-in method");
        }
    }

    private void requireOtp(User user, String purpose, String otp) {
        if (otp == null || !otp.matches("[0-9]{6}")
                || !(purpose + otp).equals(user.getResetOtp())
                || user.getOtpExpiryDate() == null
                || !user.getOtpExpiryDate().isAfter(LocalDateTime.now())) {
            throw error(HttpStatus.BAD_REQUEST, "Verification code is invalid or expired");
        }
    }

    private void clearOtp(User user) { user.setResetOtp(null); user.setOtpExpiryDate(null); }
    private String newOtp() { return String.valueOf(100000 + RANDOM.nextInt(900000)); }
    private ResponseStatusException error(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }
}
