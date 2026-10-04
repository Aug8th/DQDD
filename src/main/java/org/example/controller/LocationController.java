package org.example.controller;

import org.example.dto.outdoor.LetHangOutResponse;
import org.example.entity.outdoor.Location;
import org.example.service.outdoor.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/outdoor/locations")
public class LocationController {

    private final LocationService locationService;

    // Constructor injection
    public LocationController(
            LocationService locationService
    ) {
        this.locationService = locationService;
    }


    // GET LOCATIONS BY FOOD

    @GetMapping("/food/{foodId}")
    public ResponseEntity<List<Location>> getByFood(
            @PathVariable Integer foodId
    ) {

        List<Location> locations =
                locationService.getLocationsByFood(foodId);

        return ResponseEntity.ok(locations);
    }

    // SEARCH LOCATION BY NAME

    @GetMapping("/search")
    public ResponseEntity<List<Location>> search(
            @RequestParam String name
    ) {

        List<Location> locations =
                locationService.searchByName(name);

        return ResponseEntity.ok(locations);
    }

    // GET LOCATION BY ID

    @GetMapping("/{id}")
    public ResponseEntity<Location> getById(
            @PathVariable Integer id
    ) {

        Location location =
                locationService.getLocation(id);

        return ResponseEntity.ok(location);
    }

    // get 10 locations for 10 items

    @GetMapping("/top10")
    public ResponseEntity<List<LetHangOutResponse>> getTop10FoodsWithLocations() {
        return ResponseEntity.ok(
                locationService.getTop10FoodsWithLocations()
        );
    }
}