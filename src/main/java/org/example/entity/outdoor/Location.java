package org.example.entity.outdoor;

import jakarta.persistence.*;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private OutdoorFood food;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(name = "gg_maps_rating")
    private Float ggMapsRating;

    @Column(name = "is_admin_suggested")
    private Boolean isAdminSuggested = false;

    // No-Args Constructor
    public Location() {
    }

    // All-Args Constructor
    public Location(Integer id, OutdoorFood food, String name,
                    String address, Float ggMapsRating,
                    Boolean isAdminSuggested) {
        this.id = id;
        this.food = food;
        this.name = name;
        this.address = address;
        this.ggMapsRating = ggMapsRating;
        this.isAdminSuggested = isAdminSuggested;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OutdoorFood getFood() {
        return food;
    }

    public void setFood(OutdoorFood food) {
        this.food = food;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getIsAdminSuggested() {
        return isAdminSuggested;
    }

    public void setIsAdminSuggested(Boolean adminSuggested) {
        isAdminSuggested = adminSuggested;
    }
}