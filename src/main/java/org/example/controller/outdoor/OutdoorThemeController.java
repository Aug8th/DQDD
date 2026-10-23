package org.example.controller.outdoor;


import org.example.dto.response.outdoor.OutdoorThemeResponse;
import org.example.service.outdoor.OutdoorThemeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/outdoor-themes")
@CrossOrigin(origins = "http://localhost:5173")
public class OutdoorThemeController {

    private final OutdoorThemeService outdoorThemeService;

    public OutdoorThemeController(
            OutdoorThemeService outdoorThemeService) {
        this.outdoorThemeService = outdoorThemeService;
    }

    @GetMapping("/random")
    public ResponseEntity<List<OutdoorThemeResponse>> getRandomThemes() {

        List<OutdoorThemeResponse> themes =
                outdoorThemeService.getRandomThemes();

        return ResponseEntity.ok(themes);
    }
}