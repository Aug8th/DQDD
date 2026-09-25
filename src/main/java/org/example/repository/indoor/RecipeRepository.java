package org.example.repository.indoor;

import org.example.entity.indoor.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    List<Recipe> findByCategoryId(Integer categoryId);
    List<Recipe> findByDifficulty(String difficulty);
    List<Recipe> findByCategoryIdAndDifficulty(Integer categoryId, String difficulty);
}