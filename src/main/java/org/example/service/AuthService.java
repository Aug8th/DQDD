package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.LoginRequest;
import org.example.dto.request.RegisterRequest;
import org.example.dto.response.AuthResponse; // <--- 1. IMPORT DTO RESPONSE VÀO
import org.example.entity.common.User;
import org.example.repository.UserRepository;
import org.example.security.JwtUtils;
import org.example.security.UserDetailsServiceImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    // 1. Logic Đăng ký
    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setAuthProvider("local");
        user.setRole("user");
        user.setActive(true);

        userRepository.save(user);
        return "Đăng ký tài khoản thành công!";
    }

    // 2. Logic Đăng nhập (Trả về AuthResponse chứa token, id và email)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống!"));

        if (!user.isActive()) {
            throw new RuntimeException("Tài khoản của bạn đã bị khóa!");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }

        // Lấy UserDetails từ UserDetailsServiceImpl để truyền đúng kiểu dữ liệu cho JwtUtils
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        // Sinh JWT token
        String jwtToken = jwtUtils.generateJwtToken(userDetails);

        // Trả về đối tượng AuthResponse chứa đầy đủ token, id và email của user
        return new AuthResponse(jwtToken, Long.valueOf(user.getId()), user.getEmail());
    }
}