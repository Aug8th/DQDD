package org.example.controller.outdoor;


import lombok.RequiredArgsConstructor;
import org.example.entity.outdoor.OutdoorFood;
import org.example.service.outdoor.OutdoorFoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/outdoor/foods")
public class OutdoorFoodController {

    private final OutdoorFoodService outdoorFoodService;

    //get ALL foods
    @GetMapping
    public ResponseEntity<List<OutdoorFood>> getAllFoods() {
        return ResponseEntity.ok(outdoorFoodService.getAllFoods());
    }

    //get 6 random foods
    @GetMapping("/random")
    public ResponseEntity<List<OutdoorFood>> getRandom6Foods() {
        return ResponseEntity.ok(
                outdoorFoodService.getRandom6Foods()
        );
    }
//get food by theme
    @GetMapping("/theme/{themeId}")
    public ResponseEntity<List<OutdoorFood>> getFoodsByTheme(
            @PathVariable Integer themeId
    ) {
        return ResponseEntity.ok(
                outdoorFoodService.getFoodsByTheme(themeId)
        );
    }

}