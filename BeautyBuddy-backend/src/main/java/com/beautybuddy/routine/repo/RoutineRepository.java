package com.beautybuddy.routine.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.routine.entity.Routine;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    
}
