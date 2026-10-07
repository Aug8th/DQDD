package org.example.dto.response;

public record LocationResponse(
        Integer id,
        Integer foodId,
        String name,
        String address,
        Float ggMapsRating,
        boolean adminSuggested
) {
}
