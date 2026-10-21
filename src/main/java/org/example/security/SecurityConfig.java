package org.example.security;

import org.example.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository users) {
        return email -> {
            var user = users.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            if (!"local".equals(user.getAuthProvider()) || user.getPasswordHash() == null) {
                throw new UsernameNotFoundException("Local password login unavailable");
            }
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPasswordHash())
                    .roles(user.getRole().toUpperCase())
                    .disabled(!user.isActive())
                    .build();
        };
    }

    // Cấu hình nguồn CORS trực tiếp cho Spring Security
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    // Đăng ký AuthTokenFilter làm một Bean để Spring quản lý
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Kích hoạt CORS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không dùng Session
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/outdoor/locations/**").permitAll()
                        .requestMatchers("/api/outdoor-themes/**", "/api/outdoor/foods/**").permitAll()
                        // MỞ CÔNG KHAI TOÀN BỘ API RECIPES CHO TẤT CẢ MỌI NGƯỜI
                        .requestMatchers("/api/recipes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/uploads/**", "/api/recipes/public",
                                "/api/locations", "/api/feedbacks/public").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                // GẮN BỘ LỌC JWT VÀO TRƯỚC BỘ LỌC XÁC THỰC USERNAME/PASSWORD MẶC ĐỊNH
                .addFilterBefore(authenticationJwtTokenFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}