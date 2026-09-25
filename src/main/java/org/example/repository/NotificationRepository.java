package org.example.repository;

import org.example.entity.common.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification,Integer> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);

    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(Integer userId);

    Optional<Notification> findByIdAndUserId(Integer id,Integer userId);
}
