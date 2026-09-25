package org.example.service.notification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.example.dto.response.NotificationResponse;
import org.example.entity.common.Notification;
import org.example.entity.common.User;
import org.example.repository.NotificationRepository;
import org.example.repository.UserFridgeRepository;
import org.example.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NotificationService {
    private final UserFridgeRepository fridges;
    private final NotificationRepository notifications;
    private final UserRepository users;

    public NotificationService(UserFridgeRepository fridges,
                               NotificationRepository notifications,
                               UserRepository users) {
        this.fridges = fridges;
        this.notifications = notifications;
        this.users = users;
    }

    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void createExpiryWarnings() {
        var today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        var expiringItems = fridges.findByNotifiedFlagFalseAndExpiryDateBetween(
                today.plusDays(1), today.plusDays(2));

        for (var item : expiringItems) {
            String message = "Nguyên liệu " + item.getIngredientName()
                    + " sẽ hết hạn ngày " + item.getExpiryDate() + ". Hãy dùng sớm nhé!";
            notifications.save(new Notification(item.getUser(), message));
            item.setNotifiedFlag(true);
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> list(String email, boolean unreadOnly) {
        int userId = user(email).getId();
        var items = unreadOnly
                ? notifications.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId)
                : notifications.findByUserIdOrderByCreatedAtDesc(userId);
        return items.stream().map(NotificationResponse::from).toList();
    }

    @Transactional
    public NotificationResponse markRead(String email, Integer id) {
        var notification = notifications.findByIdAndUserId(id, user(email).getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Notification not found"));
        notification.setRead(true);
        return NotificationResponse.from(notification);
    }

    private User user(String email) {
        return users.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
