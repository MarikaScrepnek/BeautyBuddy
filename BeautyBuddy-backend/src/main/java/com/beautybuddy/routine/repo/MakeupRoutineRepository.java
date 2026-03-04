package com.beautybuddy.routine.repo;

import com.beautybuddy.routine.entity.MakeupRoutine;
import com.beautybuddy.user.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MakeupRoutineRepository extends JpaRepository<MakeupRoutine, Long> {
    Optional<MakeupRoutine> findByRoutineIdAndUserEmail(Long id, String userEmail);
    List<MakeupRoutine> findByUser(User user);
}
