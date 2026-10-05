package org.example.entity.outdoor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "wheel_items")
public class WheelItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wheel_id", nullable = false)
    private CustomWheel wheel;

    @Column(name = "item_name", nullable = false, length = 255)
    private String itemName;

    @Column(name = "is_excluded")
    private Boolean isExcluded = false;

    // No-Args Constructor
    public WheelItem() {
    }

    // All-Args Constructor
    public WheelItem(Integer id, CustomWheel wheel, String itemName,
                     Boolean isExcluded) {
        this.id = id;
        this.wheel = wheel;
        this.itemName = itemName;
        this.isExcluded = isExcluded;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CustomWheel getWheel() {
        return wheel;
    }

    public void setWheel(CustomWheel wheel) {
        this.wheel = wheel;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Boolean getIsExcluded() {
        return isExcluded;
    }

    public void setIsExcluded(Boolean excluded) {
        isExcluded = excluded;
    }
}