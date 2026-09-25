package org.example.repository;

import org.example.entity.indoor.UserFridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;

public interface UserFridgeRepository extends JpaRepository<UserFridge,Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<UserFridge> findByNotifiedFlagFalseAndExpiryDateBetween(LocalDate from,LocalDate to);
}
