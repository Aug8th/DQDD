package org.example.repository.indoor;

import org.example.entity.indoor.UserFridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFridgeRepository extends JpaRepository<UserFridge, Integer> {
    List<UserFridge> findByUserId(Integer userId);
}