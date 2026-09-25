package org.example.controller;


import org.example.entity.outdoor.Location;
import org.example.repository.outdoor.LocationRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationRepository locationRepository;

    public LocationController(
            LocationRepository locationRepository) {

        this.locationRepository = locationRepository;
    }

    // GET ALL LOCATIONS


    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {

        return ResponseEntity.ok(
                locationRepository.findAll()
        );
    }


    // SEARCH BY AREA


    @GetMapping(params = "area")
    public ResponseEntity<List<Location>> getLocationsByArea(
            @RequestParam String area) {

        List<Location> locations =
                locationRepository
                        .findByAddressContainingIgnoreCase(area);

        return ResponseEntity.ok(locations);
    }

    // GET LOCATION BY ID


    @GetMapping("/{id}")
    public ResponseEntity<?> getLocationById(
            @PathVariable Integer id) {

        return locationRepository
                .findById(id)
                .map(location ->
                        ResponseEntity.ok(location)
                )
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }
}