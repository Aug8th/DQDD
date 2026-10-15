package org.example.repository;

import org.example.entity.chatbot.AiRecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface AiRecipeIngredientRepository
        extends JpaRepository<AiRecipeIngredient, Integer> {

    List<AiRecipeIngredient> findByRecipeIdOrderByIdAsc(
            Integer recipeId
    );
}