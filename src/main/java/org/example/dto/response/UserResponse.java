package org.example.dto.response;

import org.example.entity.common.User;
import java.time.LocalDateTime;

public record UserResponse(
        Integer id,
        String email,
        String authProvider,
        String role,
        String avatarUrl,
        LocalDateTime createdAt
) {
    public static UserResponse from(User u){
        return new UserResponse(
                u.getId(),
                u.getEmail(),
                u.getAuthProvider(),
                u.getRole(),
                u.getAvatarUrl(),
                u.getCreatedAt());
    }
}
