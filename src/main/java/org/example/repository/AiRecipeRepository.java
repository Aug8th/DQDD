package org.example.repository;

import org.example.entity.chatbot.AiRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface AiRecipeRepository
        extends JpaRepository<AiRecipe, Integer> {

    List<AiRecipe> findAllByOrderByCreatedAtDescIdDesc();

    List<AiRecipe> findBySourceMessageIdAndUserIdOrderByIdAsc(Integer messageId, Integer userId);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select r from AiRecipe r where r.id = :id")
    Optional<AiRecipe> findForPublishing(@org.springframework.data.repository.query.Param("id") Integer id);

    List<AiRecipe> findByUserIdOrderByCreatedAtDescIdDesc(
            Integer userId
    );

    Optional<AiRecipe> findByIdAndUserId(
            Integer id,
            Integer userId
    );
}