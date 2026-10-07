package org.example.controller.admin;

import java.util.List;
import org.example.dto.request.LocationAdminRequest;
import org.example.dto.response.LocationResponse;
import org.example.entity.outdoor.Location;
import org.example.entity.outdoor.OutdoorFood;
import org.example.mapper.LocationMapper;
import org.example.repository.LocationRepository;
import org.example.repository.OutdoorFoodRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/locations")
public class LocationAdminController {
    private final LocationRepository locations;
    private final OutdoorFoodRepository foods;

    public LocationAdminController(LocationRepository locations, OutdoorFoodRepository foods) {
        this.locations = locations;
        this.foods = foods;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<LocationResponse> list(@RequestParam(required = false) Integer foodId) {
        var items = foodId == null ? locations.findAll() : locations.findByFoodIdOrderByIdAsc(foodId);
        return items.stream().map(LocationMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public LocationResponse get(@PathVariable Integer id) {
        return LocationMapper.toResponse(find(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationResponse create(@RequestBody LocationAdminRequest request) {
        OutdoorFood food = validateAndGetFood(request);
        var location = new Location(food, request.name().trim(), request.address().trim(),
                request.ggMapsRating(), request.adminSuggested());
        return LocationMapper.toResponse(locations.save(location));
    }

    @PutMapping("/{id}")
    @Transactional
    public LocationResponse update(@PathVariable Integer id, @RequestBody LocationAdminRequest request) {
        var location = find(id);
        OutdoorFood food = validateAndGetFood(request);
        location.update(food, request.name().trim(), request.address().trim(),
                request.ggMapsRating(), request.adminSuggested());
        return LocationMapper.toResponse(location);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        locations.delete(find(id));
    }

    private Location find(Integer id) {
        return locations.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));
    }

    private OutdoorFood validateAndGetFood(LocationAdminRequest request) {
        if (request == null || request.foodId() == null
                || request.name() == null || request.name().isBlank() || request.name().trim().length() > 255
                || request.address() == null || request.address().isBlank()
                || request.address().trim().length() > 255
                || (request.ggMapsRating() != null && (!Float.isFinite(request.ggMapsRating())
                || request.ggMapsRating() < 0 || request.ggMapsRating() > 5))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid location fields");
        }
        return foods.findById(request.foodId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Outdoor food does not exist"));
    }
}
