package com.beautybuddy.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.beautybuddy.repository.UserRepository;
import com.beautybuddy.model.User;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    public void register(String username, String email, String rawPassword) {
        String hash = encoder.encode(rawPassword);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(hash);

        userRepo.save(user);
    }

    public boolean login(String username, String rawPassword) {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        return encoder.matches(rawPassword, user.getPasswordHash());
    }
}
