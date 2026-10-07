package org.example.mapper;

import org.example.dto.response.UserResponse;
import org.example.entity.common.User;

@lombok.experimental.UtilityClass
public class UserMapper {



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