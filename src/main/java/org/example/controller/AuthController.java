package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.RegisterRequest;
import org.example.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController("accountRecoveryController")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> register(@RequestBody RegisterRequest request) {
        return Map.of("message", authService.register(request), "verificationRequired", true);
    }

    @PostMapping("/verify")
    public Map<String, String> verifyAccount(@RequestParam String email, @RequestParam String otp) {
        return Map.of("message", authService.verifyAccount(email, otp));
    }

    @PostMapping("/change-password")
    public Map<String, String> changePassword(Authentication authentication,
                                              @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authentication.getName(), request.oldPassword(), request.newPassword());
        return Map.of("message", "Password changed successfully");
    }

    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.email());
        return Map.of("message", "A password reset code has been sent to your email");
    }

    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.email(), request.otp(), request.newPassword());
        return Map.of("message", "Password reset successfully");
    }

    public record ChangePasswordRequest(String oldPassword, String newPassword) {}
    public record ForgotPasswordRequest(String email) {}
    public record ResetPasswordRequest(String email, String otp, String newPassword) {}
}
