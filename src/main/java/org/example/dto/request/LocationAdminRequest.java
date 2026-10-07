package org.example.dto.request;

public record LocationAdminRequest(
        Integer foodId,
        String name,
        String address,
        Float ggMapsRating,
        boolean adminSuggested
) {
}
