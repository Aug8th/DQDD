package org.example.repository;

import org.example.entity.CustomWheel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomWheelRepository
        extends JpaRepository<CustomWheel, Integer> {

    List<CustomWheel> findAll();
    List<CustomWheel> findByUserId(Integer userId);
    List<CustomWheel> findByNameContainingIgnoreCase(String name);
}