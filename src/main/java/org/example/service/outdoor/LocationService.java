package org.example.service.outdoor;


import lombok.RequiredArgsConstructor;
import org.example.dto.outdoor.LetHangOutResponse;
import org.example.entity.Location;
import org.example.repository.BeuDuong.LocationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LocationService {

    private final LocationRepository locationRepository;


    // GET ALL Locations
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
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

    //Get 10 Locations for 10 Items

    public List<LetHangOutResponse> getTop10FoodsWithLocations() {
        return locationRepository.findTop10FoodsWithLocations(
                PageRequest.of(0, 10)
        );
    }
}