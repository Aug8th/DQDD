package org.example.mapper;

import org.example.dto.response.LocationResponse;
import org.example.entity.outdoor.Location;

@lombok.experimental.UtilityClass
public class LocationMapper {


    public static LocationResponse toResponse(Location l) {
        return new LocationResponse(
                l.getId(),
                l.getFood().getId(),
                l.getName(),
                l.getAddress(),
                l.getGgMapsRating(),
                l.isAdminSuggested()
        );
    }
}
