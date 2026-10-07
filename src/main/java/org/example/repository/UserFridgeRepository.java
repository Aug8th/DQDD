package org.example.repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import org.example.entity.indoor.UserFridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

@org.springframework.stereotype.Repository
public interface UserFridgeRepository extends JpaRepository<UserFridge, Integer> {
    List<UserFridge> findByUserId(Integer userId);
    List<UserFridge> findByUserIdAndStorageType(Integer userId, String storageType);

    default List<UserFridge> findByUserIdAndStorageType(Integer userId, UserFridge.StorageType type) {
        return findByUserIdAndStorageType(userId, type == null ? null : type.name());
    }

    default List<UserFridge> findByExpiryDateLessThanEqualAndExpiryDateGreaterThanEqualAndNotifiedFlagFalse(
            LocalDate endDate, LocalDate startDate) {
        return findByNotifiedFlagFalseAndExpiryDateBetween(startDate, endDate);
    }

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

    List<UserFridge> findByUserIdOrderByExpiryDateAsc(
            Integer userId,
            org.springframework.data.domain.Pageable pageable
    );
}
