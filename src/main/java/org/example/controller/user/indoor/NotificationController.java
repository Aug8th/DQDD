package org.example.controller.user.indoor;

import lombok.RequiredArgsConstructor;
import org.example.entity.indoor.UserNotification;
import org.example.repository.indoor.UserNotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final UserNotificationRepository notificationRepository;

    // 1. Lấy danh sách thông báo của user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserNotification>> getNotificationsByUser(@PathVariable Integer userId) {
        List<UserNotification> notifications = notificationRepository.findAll()
                .stream()
                .filter(n -> n.getUserId().equals(userId))
                .toList();
        return ResponseEntity.ok(notifications);
    }

    // 2. Đánh dấu thông báo đã đọc
    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Integer id) {
        UserNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo!"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
        return ResponseEntity.ok("Đã đánh dấu đọc thông báo!");
    }
}