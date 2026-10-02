package org.example.mapper;

import org.example.dto.response.UserResponse;
import org.example.entity.common.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User u) {
        return new UserResponse(
                u.getId(),
                u.getEmail(),
                u.getAuthProvider(),
                u.getRole(),
                u.getAvatarUrl(),
                u.isActive(),
                u.getCreatedAt()
        );
    }
}