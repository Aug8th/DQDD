package org.example.repository.BeuDuong;


import org.example.entity.BeuDuong.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.example.dto.outdoor.LetHangOutResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LocationRepository
        extends JpaRepository<Location, Integer> {

    List<Location> findByFoodId(Integer foodId);



    List<Location> findByNameContainingIgnoreCase(String name);

    @Query("""
    SELECT new org.example.dto.outdoor.LetHangOutResponse(
        f.id,
        f.name,
        l.id,
        l.name,
        l.address,
        l.ggMapsRating
    )
    FROM Location l
    JOIN l.food f
    WHERE l.id IN (
        SELECT MAX(l2.id)
        FROM Location l2
        GROUP BY l2.food.id
    )
    ORDER BY l.ggMapsRating DESC
""")
    List<LetHangOutResponse> findTop10FoodsWithLocations(Pageable pageable);

}