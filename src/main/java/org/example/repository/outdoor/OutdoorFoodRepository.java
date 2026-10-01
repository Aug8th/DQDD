package org.example.repository.outdoor;

import org.example.entity.outdoor.OutdoorFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutdoorFoodRepository extends JpaRepository<OutdoorFood, Integer> {

    @Query(value = "SELECT * FROM outdoor_foods ORDER BY RAND() LIMIT 6", nativeQuery = true)
    List<OutdoorFood> findRandom6();
}