package org.example.repository.BeuDuong;

import org.example.entity.WheelItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WheelItemRepository
        extends JpaRepository<WheelItem, Integer> {

    List<WheelItem> findByWheelId(Integer wheelId);
}