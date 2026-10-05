package org.example.controller.outdoor;


import org.example.entity.outdoor.OutdoorFood;
import org.example.service.outdoor.OutdoorFoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/outdoor/foods")
public class OutdoorFoodController {

    private final OutdoorFoodService outdoorFoodService;

    public OutdoorFoodController(OutdoorFoodService outdoorFoodService) {
        this.outdoorFoodService = outdoorFoodService;
    }

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
//get food
    @GetMapping("/theme/{themeId}")
    public ResponseEntity<List<OutdoorFood>> getFoodsByTheme(
            @PathVariable Integer themeId
    ) {
        return ResponseEntity.ok(
                outdoorFoodService.getFoodsByTheme(themeId)
        );
    }

}