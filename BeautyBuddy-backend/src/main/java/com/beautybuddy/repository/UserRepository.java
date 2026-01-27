package com.beautybuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beautybuddy.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
