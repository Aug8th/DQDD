package org.example.dto.response;

import java.time.LocalDateTime;

public record NotificationResponse(
        Integer id,
        String message,
        boolean isRead,
        LocalDateTime createdAt
) {
}
