package org.example.mapper;

import org.example.dto.response.OutdoorFoodOptionResponse;
import org.example.entity.outdoor.OutdoorFood;

@lombok.experimental.UtilityClass
public class OutdoorFoodMapper {


    public static OutdoorFoodOptionResponse toOptionResponse(OutdoorFood f) {
        return new OutdoorFoodOptionResponse(
                f.getId(),
                f.getName(),
                f.getThemeId()
        );
    }
}
