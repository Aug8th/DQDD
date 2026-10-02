package org.example.controller.admin;

import java.util.List;
import org.example.dto.response.OutdoorFoodOptionResponse;
import org.example.mapper.OutdoorFoodMapper;
import org.example.repository.OutdoorFoodRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/outdoor-foods")
public class OutdoorFoodAdminController {
    private final OutdoorFoodRepository foods;

    public OutdoorFoodAdminController(OutdoorFoodRepository foods) {
        this.foods = foods;
    }

    @GetMapping
    public List<OutdoorFoodOptionResponse> list() {
        return foods.findAll().stream()
                .map(OutdoorFoodMapper::toOptionResponse)
                .toList();
    }
}
