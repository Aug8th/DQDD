package org.example.entity.outdoor;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "custom_wheels")
public class CustomWheel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "wheel_type", nullable = false)
    private WheelType wheelType;

    @Column(length = 255)
    private String name;

    @OneToMany(
            mappedBy = "wheel",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<WheelItem> items = new ArrayList<>();

    public CustomWheel() {
    }

    public CustomWheel(
            Integer id,
            Integer userId,
            WheelType wheelType,
            String name,
            List<WheelItem> items
    ) {
        this.id = id;
        this.userId = userId;
        this.wheelType = wheelType;
        this.name = name;
        this.items = items;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public WheelType getWheelType() {
        return wheelType;
    }

    public void setWheelType(WheelType wheelType) {
        this.wheelType = wheelType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WheelItem> getItems() {
        return items;
    }

    public void setItems(List<WheelItem> items) {
        this.items = items;
    }
}