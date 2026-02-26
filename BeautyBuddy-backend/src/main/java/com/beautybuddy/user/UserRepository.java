package com.beautybuddy.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
