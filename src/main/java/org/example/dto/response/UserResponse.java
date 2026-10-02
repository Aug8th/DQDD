package org.example.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
        Integer id,
        String email,
        String authProvider,
        String role,
        String avatarUrl,
        boolean active,
        LocalDateTime createdAt
) {
}