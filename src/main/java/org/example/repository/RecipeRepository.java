package org.example.repository;

import java.util.List;
import org.example.entity.indoor.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
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
}
