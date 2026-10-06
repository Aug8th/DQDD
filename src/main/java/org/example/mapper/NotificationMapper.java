package org.example.mapper;

import org.example.dto.response.NotificationResponse;
import org.example.entity.common.Notification;

@lombok.experimental.UtilityClass
public class NotificationMapper {


    public static NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getMessage(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}
