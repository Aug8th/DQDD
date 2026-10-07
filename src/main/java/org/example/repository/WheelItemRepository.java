package org.example.repository;

import org.example.entity.outdoor.WheelItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WheelItemRepository
        extends JpaRepository<WheelItem, Integer> {

    List<WheelItem> findByWheelId(Integer wheelId);
}