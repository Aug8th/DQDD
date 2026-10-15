package org.example.entity.chatbot;

import jakarta.persistence.*;
import org.example.entity.common.User;

import java.time.LocalDateTime;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@Entity
@Table(name = "ai_recipes")
public class AiRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "calories_kcal")
    private Integer caloriesKcal;

    @Column(name = "cooking_tip", columnDefinition = "TEXT")
    private String cookingTip;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "dish_type", columnDefinition = "enum('MAIN_DISH','DRINK','DESSERT')")
    private String dishType = "MAIN_DISH";

    @Column(columnDefinition = "enum('ASIAN','EUROPEAN','AMERICAN','AFRICAN','OCEANIAN','STREET_FOOD')")
    private String region;

    @Column(length = 100)
    private String country;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "video_url", length = 255)
    private String videoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "published_recipe_id")
    private org.example.entity.indoor.Recipe publishedRecipe;

    public void setDishType(String value) { dishType = value; }
    public void setRegion(String value) { region = value; }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_message_id")
    private ChatMessage sourceMessage;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "prep_time")
    private Integer prepTime;

    private Integer servings;

    @Column(columnDefinition = "enum('easy','medium','hard')")
    private String difficulty;

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String instructions;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public AiRecipe(
            User user,
            ChatMessage sourceMessage,
            String name,
            String description,
            Integer prepTime,
            Integer servings,
            String difficulty,
            String instructions
    ) {
        this.user = user;
        this.sourceMessage = sourceMessage;
        this.name = name;
        this.description = description;
        this.prepTime = prepTime;
        this.servings = servings;
        this.difficulty = difficulty;
        this.instructions = instructions;
    }

}