package org.example.repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import org.example.entity.indoor.UserFridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

public interface UserFridgeRepository extends JpaRepository<UserFridge, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<UserFridge> findByNotifiedFlagFalseAndExpiryDateBetween(LocalDate from, LocalDate to);

    long countByExpiryDateBefore(LocalDate date);

    long countByExpiryDateBetween(LocalDate from, LocalDate to);

    long countByStorageType(String storageType);

    @Query("select f.ingredientName as ingredientName, count(f) as itemCount "
            + "from UserFridge f group by f.ingredientName "
            + "having count(distinct f.user.id) >= 3 order by count(f) desc")
    List<IngredientCount> topIngredients(Pageable pageable);

    interface IngredientCount {
        String getIngredientName();

        long getItemCount();
    }
}
