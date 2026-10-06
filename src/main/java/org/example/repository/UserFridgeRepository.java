package org.example.repository;

import org.example.entity.indoor.UserFridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserFridgeRepository extends JpaRepository<UserFridge, Integer> {
    List<UserFridge> findByUserId(Integer userId);
    List<UserFridge> findByUserIdAndStorageType(Integer userId, UserFridge.StorageType storageType);
    List<UserFridge> findByExpiryDateLessThanEqualAndExpiryDateGreaterThanEqualAndNotifiedFlagFalse(
            LocalDate endDate, LocalDate startDate
    );
}