package com.beautybuddy.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;

import com.beautybuddy.repository.UserRepository;
import com.beautybuddy.wishlists.Wishlist;
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
        email = email.trim().toLowerCase();
        String hash = encoder.encode(rawPassword);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(hash);

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setWishlist(wishlist);

        userRepo.save(user);
    }

    public boolean login(String email, String rawPassword) {
        email = email.trim().toLowerCase();
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        return encoder.matches(rawPassword, user.getPasswordHash());
    }
}
