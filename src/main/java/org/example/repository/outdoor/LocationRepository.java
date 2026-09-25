package org.example.repository.outdoor;

import org.example.entity.outdoor.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository
        extends JpaRepository<Location, Integer> {

    List<Location> findByAddressContainingIgnoreCase(
            String area
    );
}