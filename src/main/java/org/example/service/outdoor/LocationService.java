package org.example.service.outdoor;


import org.example.entity.outdoor.Location;
import org.example.repository.outdoor.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    // Constructor injection
    public LocationService(
            LocationRepository locationRepository
    ) {
        this.locationRepository = locationRepository;
    }

    // GET LOCATIONS BY FOOD

    public List<Location> getLocationsByFood(
            Integer foodId
    ) {

        return locationRepository.findByFoodId(foodId);
    }

    // SEARCH LOCATION BY NAME


    public List<Location> searchByName(
            String name
    ) {

        return locationRepository
                .findByNameContainingIgnoreCase(name);
    }


    // GET LOCATION BY ID

    public Location getLocation(Integer id) {

        return locationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Location not found"
                        )
                );
    }
}