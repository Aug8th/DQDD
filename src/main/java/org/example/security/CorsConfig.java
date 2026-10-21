package org.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Cho phép gửi credentials (token, cookies...)
        config.setAllowCredentials(true);

        // Cho phép cổng của React (Vite chạy ở port 5173) truy cập
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));

        // Cho phép tất cả các Header
        config.setAllowedHeaders(List.of("*"));

        // Cho phép tất cả các Method (GET, POST, PUT, DELETE, OPTIONS...)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}