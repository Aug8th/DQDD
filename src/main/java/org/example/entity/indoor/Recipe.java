package org.example.entity.indoor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.example.entity.common.User;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@Entity
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "calories_kcal")
    private Integer caloriesKcal;

    @Column(name = "cooking_tip", columnDefinition = "TEXT")
    private String cookingTip;

    @Column(name = "is_recommended", nullable = false)
    private boolean recommended;

    @jakarta.persistence.OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY,
            cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @jakarta.persistence.OrderBy("id ASC")
    private java.util.List<RecipeIngredient> ingredients = new java.util.ArrayList<>();

    @Column(name = "category_id")
    private Integer categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    public enum DishType { MAIN_DISH, DRINK, DESSERT }
    public enum Region { ASIAN, EUROPEAN, AMERICAN, AFRICAN, OCEANIAN, STREET_FOOD }

    private String name;

    @Column(name = "prep_time")
    private Integer prepTime;

    private Integer servings;

    @Column(columnDefinition = "enum('dễ','trung bình','khó')")
    private String difficulty;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "dish_type", columnDefinition = "enum('MAIN_DISH','DRINK','DESSERT')")
    private String dishType = "MAIN_DISH";

    @Column(columnDefinition = "enum('ASIAN','EUROPEAN','AMERICAN','AFRICAN','OCEANIAN','STREET_FOOD')")
    private String region;

    @Column(length = 100)
    private String country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_user_id")
    private User submittedBy;

    @Column(name = "approval_status", columnDefinition = "enum('pending','approved','rejected')")
    private String approvalStatus;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public void setDishType(DishType type) { this.dishType = type == null ? null : type.name(); }
    public void setRegion(Region region) { this.region = region == null ? null : region.name(); }

    public void setDishType(String dishType) {
        this.dishType = dishType;
    }

    public void setRegion(String region) {
        this.region = region;
    }

}
