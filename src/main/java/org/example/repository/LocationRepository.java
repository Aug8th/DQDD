package org.example.repository;

import java.util.List;
import org.example.entity.outdoor.Location;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByFoodIdOrderByIdAsc(Integer foodId);
}
