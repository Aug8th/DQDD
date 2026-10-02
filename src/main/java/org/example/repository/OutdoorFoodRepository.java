package org.example.repository;

import org.example.entity.outdoor.OutdoorFood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutdoorFoodRepository extends JpaRepository<OutdoorFood, Integer> {
}
