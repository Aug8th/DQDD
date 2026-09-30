package org.example.controller;

import org.example.entity.outdoor.OutdoorFood;
import org.example.repository.outdoor.OutdoorFoodRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/outdoor-foods")
public class OutdoorFoodController {

    private final OutdoorFoodRepository outdoorFoodRepository;

    public OutdoorFoodController(
            OutdoorFoodRepository outdoorFoodRepository) {
        this.outdoorFoodRepository = outdoorFoodRepository;
    }

    // Random 6 foods appear Interface
    @GetMapping("/random")
    public ResponseEntity<List<OutdoorFood>> getRandomFoods() {

        List<OutdoorFood> foods =
                outdoorFoodRepository.findAll();

        Collections.shuffle(foods);

        List<OutdoorFood> result =
                foods.stream()
                        .limit(6)
                        .toList();

        return ResponseEntity.ok(result);
    }
}