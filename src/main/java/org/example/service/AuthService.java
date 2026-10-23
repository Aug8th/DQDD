package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.LoginRequest;
import org.example.dto.request.RegisterRequest;
import org.example.dto.response.AuthResponse;
import org.example.entity.common.User;
import org.example.repository.UserRepository;
import org.example.security.JwtUtils;
import org.example.security.UserDetailsServiceImpl;
import org.example.service.GoogleTokenService;
import org.example.service.notification.EmailService; // Service gửi mail có sẵn của nhóm
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final GoogleTokenService googleTokenService;
    private final EmailService emailService; // Tiêm EmailService sẵn có để gửi OTP

    // 1. Bước 1 của Đăng ký: Tạo tài khoản tạm thời & Gửi mã OTP xác thực email thật
    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }

        // Tạo mã OTP ngẫu nhiên 6 chữ số để xác thực email
        String otp = String.valueOf((int) ((Math.random() * 900000) + 100000));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setAuthProvider("local");
        user.setRole("user");
        user.setActive(false); // Chưa kích hoạt cho đến khi nhập đúng OTP xác thực email thật
        user.setResetOtp(otp);
        user.setOtpExpiryDate(LocalDateTime.now().plusMinutes(10)); // OTP có hiệu lực 10 phút

        userRepository.save(user);

        // Gửi mã OTP qua email để kiểm tra xem user có dùng email thật hay không
        String content = "Mã OTP xác thực đăng ký tài khoản của bạn là: " + otp + ". Mã có hiệu lực trong vòng 10 phút.";
        // Lưu ý: Nếu EmailService của nhóm đang dùng cố định tiêu đề, ta có thể gọi trực tiếp:
        emailService.sendExpiryWarning(request.getEmail(), content);

        return "Mã xác thực đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư để kích hoạt tài khoản!";
    }

    // 1b. Bước 2 của Đăng ký: Nhập OTP để kích hoạt tài khoản chính thức
    public String verifyAccount(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        if (user.isActive()) {
            return "Tài khoản đã được kích hoạt trước đó rồi!";
        }

        if (user.getResetOtp() == null || !user.getResetOtp().equals(otp)) {
            throw new RuntimeException("Mã OTP xác thực không hợp lệ!");
        }

        if (user.getOtpExpiryDate() != null && user.getOtpExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn!");
        }

        // Kích hoạt tài khoản thành công
        user.setActive(true);
        user.setResetOtp(null);
        user.setOtpExpiryDate(null);
        userRepository.save(user);

        return "Kích hoạt tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.";
    }

    // 2. Logic Đăng nhập
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống!"));

        if (!user.isActive()) {
            throw new RuntimeException("Tài khoản chưa được kích hoạt hoặc đã bị khóa!");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String jwtToken = jwtUtils.generateJwtToken(userDetails);

        return new AuthResponse(jwtToken, Long.valueOf(user.getId()), user.getEmail());
    }

    // 3. Logic Đổi mật khẩu (Khi đã đăng nhập)
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác!");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 4. Logic Quên mật khẩu - Gửi mã OTP về Email
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email này chưa đăng ký tài khoản!"));

        String otp = String.valueOf((int) ((Math.random() * 900000) + 100000));
        user.setResetOtp(otp);
        user.setOtpExpiryDate(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        String content = "Mã OTP đặt lại mật khẩu của bạn là: " + otp + ". Mã có hiệu lực trong vòng 15 phút.";
        emailService.sendExpiryWarning(email, content);
    }

    // 5. Logic Đặt lại mật khẩu mới bằng OTP
    public void resetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        if (user.getResetOtp() == null || !user.getResetOtp().equals(otp)) {
            throw new RuntimeException("Mã OTP không hợp lệ!");
        }

        if (user.getOtpExpiryDate() != null && user.getOtpExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn!");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetOtp(null);
        user.setOtpExpiryDate(null);
        userRepository.save(user);
    }

    // 6. Logic Đăng nhập bằng Google
    public AuthResponse googleLogin(String credential) {
        var identity = googleTokenService.verify(credential);
        String email = identity.email().toLowerCase(Locale.ROOT);

        User user = userRepository.findByGoogleSub(identity.subject()).orElseGet(() -> {
            return userRepository.findByEmail(email).map(existingUser -> {
                existingUser.setGoogleSub(identity.subject());
                existingUser.setAuthProvider("google");
                existingUser.setActive(true); // Google tự động active luôn vì đã xác thực danh tính
                return userRepository.save(existingUser);
            }).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setGoogleSub(identity.subject());
                newUser.setAuthProvider("google");
                newUser.setRole("user");
                newUser.setActive(true);
                return userRepository.save(newUser);
            });
        });

        if (!user.isActive()) {
            throw new RuntimeException("Tài khoản của bạn đã bị khóa!");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String jwtToken = jwtUtils.generateJwtToken(userDetails);

        return new AuthResponse(jwtToken, Long.valueOf(user.getId()), user.getEmail());
    }
}