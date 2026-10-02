package org.example.repository;

import java.util.List;
import org.example.entity.common.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findAllByOrderByCreatedAtDesc();

    List<Feedback> findByModerationStatusOrderByCreatedAtDesc(String moderationStatus);
}
