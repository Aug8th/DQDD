package org.example.repository.outdoor;

import org.example.entity.outdoor.WheelItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WheelItemRepository extends JpaRepository<WheelItem, Integer> {
}