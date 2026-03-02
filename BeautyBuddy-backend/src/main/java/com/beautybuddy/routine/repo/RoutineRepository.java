package com.beautybuddy.routine.repo;

import com.beautybuddy.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<Routine, Long> {    
}
