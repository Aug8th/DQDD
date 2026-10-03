package org.example.repository.outdoor;

import org.example.entity.outdoor.WheelHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WheelHistoryRepository extends JpaRepository<WheelHistory, Integer> {

    List<WheelHistory> findByWheelIdOrderByCreatedAtDesc(Integer wheelId);
}