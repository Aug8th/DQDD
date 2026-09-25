package org.example.entity.outdoor;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "outdoor_foods")
public class OutdoorFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "theme_id")
    private Integer themeId;

    @Column(nullable = false, length = 255)
    private String name;

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Location> locations = new ArrayList<>();

    // No-Args Constructor
    public OutdoorFood() {
    }

    // All-Args Constructor
    public OutdoorFood(Integer id, Integer themeId, String name, List<Location> locations) {
        this.id = id;
        this.themeId = themeId;
        this.name = name;
        this.locations = locations;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getThemeId() {
        return themeId;
    }

    public void setThemeId(Integer themeId) {
        this.themeId = themeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}