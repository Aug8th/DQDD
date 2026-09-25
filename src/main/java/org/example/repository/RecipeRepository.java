package org.example.repository;

import org.example.entity.indoor.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe,Integer> {
    List<Recipe> findBySubmittedByIsNotNullOrderByCreatedAtDesc();

    List<Recipe> findBySubmittedByIsNotNullAndApprovalStatusOrderByCreatedAtDesc(String status);

    List<Recipe> findByApprovalStatusOrderByCreatedAtDesc(String status);
}
