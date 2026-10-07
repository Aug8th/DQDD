package org.example.repository;

import org.example.entity.indoor.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Integer> {
    java.util.List<RecipeIngredient> findByRecipeId(Integer recipeId);
}

