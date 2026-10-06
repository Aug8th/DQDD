package org.example.service.indoor;

import lombok.RequiredArgsConstructor;
import org.example.entity.indoor.UserFridge;
import org.example.entity.indoor.UserNotification;
import org.example.repository.indoor.UserRepository;
import org.example.repository.UserFridgeRepository;
import org.example.repository.UserNotificationRepository;
import org.example.service.email.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FridgeNotificationScheduler {

    private final UserFridgeRepository fridgeRepository;
    private final UserNotificationRepository notificationRepository;
    private final EmailService emailService;
    private final UserRepository userRepository; // <-- Khai báo repository của user để lấy email

    // Cron job chạy vào 00:00 mỗi đêm (Đang để test theo phút: 0 * * * * *)
//    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void scanExpiringIngredients() {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(3); // Quét nguyên liệu hết hạn trong vòng 3 ngày tới

        List<UserFridge> expiringItems = fridgeRepository
                .findByExpiryDateLessThanEqualAndExpiryDateGreaterThanEqualAndNotifiedFlagFalse(targetDate, today);

        if (expiringItems.isEmpty()) {
            return;
        }

        Map<Integer, List<UserFridge>> itemsByUser = expiringItems.stream()
                .collect(Collectors.groupingBy(UserFridge::getUserId));

        for (Map.Entry<Integer, List<UserFridge>> entry : itemsByUser.entrySet()) {
            Integer userId = entry.getKey();
            List<UserFridge> items = entry.getValue();

            // 1. Lấy email thực tế của user từ bảng users dựa vào userId
            String userEmail = userRepository.findById(userId)
                    .map(user -> user.getEmail())
                    .orElse(null);

            if (userEmail == null || userEmail.trim().isEmpty()) {
                System.err.println("⚠️ User ID " + userId + " không tìm thấy email trong hệ thống!");
                continue;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("⚠️ Cảnh báo tủ lạnh: Bạn có ").append(items.size()).append(" nguyên liệu sắp hết hạn trong 3 ngày tới:\n\n");
            for (UserFridge item : items) {
                sb.append("- ").append(item.getIngredientName())
                        .append(" (Số lượng: ").append(item.getQuantity()).append(" ").append(item.getUnit())
                        .append(", Hạn sử dụng: ").append(item.getExpiryDate()).append(")\n");
            }
            String messageContent = sb.toString();

            // 2. Lưu thông báo vào DB để Front-end hiển thị chuông
            UserNotification notification = new UserNotification();
            notification.setUserId(userId);
            notification.setMessage(messageContent);
            notification.setIsRead(false);
            notificationRepository.save(notification);

            // 3. Gửi email và in log kết quả
            try {
                emailService.sendEmail(userEmail, "🔔 Cảnh báo: Nguyên liệu trong tủ lạnh sắp hết hạn!", messageContent);
                System.out.println("✅ Đã gửi email thành công tới: " + userEmail);
            } catch (Exception e) {
                System.err.println("❌ Gửi email thất bại cho user ID " + userId + " (" + userEmail + "): " + e.getMessage());
                e.printStackTrace(); // In vết lỗi chi tiết nếu sai cấu hình SMTP / App Password
            }

            // 4. Đánh dấu đã thông báo để không bị spam vào các lần quét sau
            for (UserFridge item : items) {
                item.setNotifiedFlag(true);
            }
            fridgeRepository.saveAll(items);
        }
    }
}