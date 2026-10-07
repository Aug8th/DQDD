package org.example.controller.user;

import java.util.List;
import org.example.dto.response.LocationResponse;
import org.example.repository.LocationRepository;
import org.example.mapper.LocationMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/locations")
public class LocationPublicController {
    private final LocationRepository locations;

    public LocationPublicController(LocationRepository locations) {
        this.locations = locations;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<LocationResponse> list(@RequestParam Integer foodId) {
        return locations.findByFoodIdOrderByIdAsc(foodId).stream()
                .map(LocationMapper::toResponse)
                .toList();
    }
}
