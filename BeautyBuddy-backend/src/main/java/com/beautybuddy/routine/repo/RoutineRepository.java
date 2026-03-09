package com.beautybuddy.routine.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.routine.entity.Routine;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findByUserIdAndCategoryName(Long userId, String categoryName);
    Optional<Routine> findByIdAndUserEmail(Long id, String userEmail);
}