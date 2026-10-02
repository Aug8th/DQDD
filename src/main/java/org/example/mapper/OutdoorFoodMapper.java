package org.example.mapper;

import org.example.dto.response.OutdoorFoodOptionResponse;
import org.example.entity.outdoor.OutdoorFood;

public final class OutdoorFoodMapper {
    private OutdoorFoodMapper() {
    }

    public static OutdoorFoodOptionResponse toOptionResponse(OutdoorFood f) {
        return new OutdoorFoodOptionResponse(
                f.getId(),
                f.getName(),
                f.getThemeId()
        );
    }
}
