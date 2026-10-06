package org.example.repository;

import java.util.List;
import org.example.entity.indoor.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@org.springframework.stereotype.Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    List<Recipe> findByApprovalStatusAndRecommendedTrueOrderByCreatedAtDesc(String status);

    List<Recipe> findByCategoryId(Integer categoryId);
    List<Recipe> findByDifficulty(String difficulty);
    List<Recipe> findByCategoryIdAndDifficulty(Integer categoryId, String difficulty);

    @Query("select r from Recipe r where r.approvalStatus = 'approved' "
            + "and (:keyword is null or lower(r.name) like lower(concat('%', :keyword, '%'))) "
            + "and (:dishType is null or r.dishType = :dishType) "
            + "and (:region is null or r.region = :region) "
            + "and (:country is null or lower(r.country) = lower(:country))")
    List<Recipe> searchApproved(@Param("keyword") String keyword,
                                @Param("dishType") String dishType, @Param("region") String region,
                                @Param("country") String country);

    default List<Recipe> searchRecipes(String keyword, Recipe.DishType dishType,
                                       Recipe.Region region, String country) {
        return searchApproved(keyword, dishType == null ? null : dishType.name(),
                region == null ? null : region.name(), country);
    }

    @Query(value = "SELECT * FROM recipes WHERE approval_status = 'approved' ORDER BY RAND() LIMIT 1",
            nativeQuery = true)
    java.util.Optional<Recipe> findRandomRecipe();

    List<Recipe> findBySubmittedByIsNotNullOrderByCreatedAtDesc();

    List<Recipe> findBySubmittedByIsNotNullAndApprovalStatusOrderByCreatedAtDesc(String status);

    List<Recipe> findByApprovalStatusOrderByCreatedAtDesc(String status);

    @Query("select r from Recipe r where (:categoryId is null or r.categoryId = :categoryId) "
            + "and (:dishType is null or r.dishType = :dishType) "
            + "and (:region is null or r.region = :region) "
            + "and (:country is null or lower(r.country) = lower(:country)) "
            + "order by r.createdAt desc")
    List<Recipe> findForAdmin(@Param("categoryId") Integer categoryId,
                              @Param("dishType") String dishType,
                              @Param("region") String region,
                              @Param("country") String country);

    @Query("select r from Recipe r where r.approvalStatus = 'approved' "
            + "and (:dishType is null or r.dishType = :dishType) "
            + "and (:region is null or r.region = :region) "
            + "and (:country is null or lower(r.country) = lower(:country)) "
            + "order by r.createdAt desc")
    List<Recipe> findPublic(@Param("dishType") String dishType,
                            @Param("region") String region,
                            @Param("country") String country);

    List<Recipe> findByApprovalStatusOrderByCreatedAtDesc(
            String approvalStatus,
            org.springframework.data.domain.Pageable pageable
    );
}
