package org.example.entity.chatbot;

import jakarta.persistence.*;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@Entity
@Table(name = "ai_recipe_ingredients")
public class AiRecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ai_recipe_id", nullable = false)
    private AiRecipe recipe;

    @Column(name = "ingredient_name", nullable = false, length = 150)
    private String ingredientName;

    @Column(length = 100)
    private String quantity;

    public AiRecipeIngredient(
            AiRecipe recipe,
            String ingredientName,
            String quantity
    ) {
        this.recipe = recipe;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
    }

}