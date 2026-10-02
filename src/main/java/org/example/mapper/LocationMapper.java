package org.example.mapper;

import org.example.dto.response.LocationResponse;
import org.example.entity.outdoor.Location;

public final class LocationMapper {
    private LocationMapper() {
    }

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
