package org.example.entity.indoor;

import jakarta.persistence.*;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "ingredient_name", nullable = false, length = 150)
    private String ingredientName;

    @Column(length = 100)
    private String quantity;

    public RecipeIngredient(Recipe recipe, String ingredientName, String quantity) {
        this.recipe = recipe;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
    }

    @Column(name = "recipe_id", insertable = false, updatable = false)
    private Integer recipeId;

}
