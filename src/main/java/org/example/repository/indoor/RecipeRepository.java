package org.example.repository.indoor;

import org.example.entity.indoor.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

    List<Recipe> findByCategoryId(Integer categoryId);

    List<Recipe> findByDifficulty(String difficulty);

    List<Recipe> findByCategoryIdAndDifficulty(Integer categoryId, String difficulty);

    //Lọc đa tầng
    @Query("SELECT r FROM Recipe r WHERE " +
            "(:keyword IS NULL OR r.name LIKE %:keyword%) AND " +
            "(:dishType IS NULL OR r.dishType = :dishType) AND " +
            "(:region IS NULL OR r.region = :region) AND " +
            "(:country IS NULL OR r.country = :country)")
    List<Recipe> searchRecipes(
            @Param("keyword") String keyword,
            @Param("dishType") Recipe.DishType dishType,
            @Param("region") Recipe.Region region,
            @Param("country") String country
    );

    //Lấy ngẫu nhiên 1 món ăn
    @Query(value = "SELECT * FROM recipes ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Recipe> findRandomRecipe();
}