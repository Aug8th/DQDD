package org.example.dto.response;

import org.example.entity.common.Notification;
import java.time.LocalDateTime;

public record NotificationResponse(
        Integer id,
        String message,
        boolean isRead,
        LocalDateTime createdAt)
{
    public static NotificationResponse from(Notification n)
    {
        return new NotificationResponse(
            n.getId(),
            n.getMessage(),
            n.isRead(),
            n.getCreatedAt());
    }
}
