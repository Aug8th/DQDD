package org.example.repository.BeuDuong;

import org.example.entity.WheelHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WheelHistoryRepository extends JpaRepository<WheelHistory, Integer> {

    List<WheelHistory> findByWheelIdOrderByCreatedAtDesc(Integer wheelId);

    void deleteByWheelId(Integer wheelId);

    void deleteByOldItemIdOrNewItemId(Integer oldItemId, Integer newItemId);
}