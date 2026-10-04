package org.example.dto.outdoor;

public class LetHangOutResponse {

    private Integer foodId;
    private String foodName;
    private Integer locationId;
    private String locationName;
    private String address;
    private Float ggMapsRating;

    public LetHangOutResponse() {
    }

    public LetHangOutResponse(
            Integer foodId,
            String foodName,
            Integer locationId,
            String locationName,
            String address,
            Float ggMapsRating
    ) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.locationId = locationId;
        this.locationName = locationName;
        this.address = address;
        this.ggMapsRating = ggMapsRating;
    }

    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Float getGgMapsRating() {
        return ggMapsRating;
    }

    public void setGgMapsRating(Float ggMapsRating) {
        this.ggMapsRating = ggMapsRating;
    }
}