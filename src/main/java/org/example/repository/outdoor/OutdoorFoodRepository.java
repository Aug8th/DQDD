package org.example.repository.outdoor;

import org.example.entity.outdoor.OutdoorFood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutdoorFoodRepository extends JpaRepository<OutdoorFood, Integer> {
}