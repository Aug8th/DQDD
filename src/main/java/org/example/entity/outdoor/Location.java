package org.example.entity.outdoor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "food_id")
    private OutdoorFood food;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(name = "gg_maps_rating")
    private Float ggMapsRating;

    @Column(name = "is_admin_suggested")
    private boolean adminSuggested;

    public Location(OutdoorFood food, String name, String address,
                    Float ggMapsRating, boolean adminSuggested) {
        this.food = food;
        this.name = name;
        this.address = address;
        this.ggMapsRating = ggMapsRating;
        this.adminSuggested = adminSuggested;
    }

    public void update(OutdoorFood food, String name, String address,
                       Float ggMapsRating, boolean adminSuggested) {
        this.food = food;
        this.name = name;
        this.address = address;
        this.ggMapsRating = ggMapsRating;
        this.adminSuggested = adminSuggested;
    }
}
