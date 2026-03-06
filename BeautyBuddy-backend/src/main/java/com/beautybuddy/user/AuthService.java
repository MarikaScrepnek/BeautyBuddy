package com.beautybuddy.user;

import com.beautybuddy.user.entity.User;
import com.beautybuddy.wishlist.entity.Wishlist;
import com.beautybuddy.routine.OccasionEnum;
import com.beautybuddy.routine.RoutineService;
import com.beautybuddy.routine.dto.CreateMakeupRoutineRequestDTO;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final RoutineService routineService;


    public AuthService(UserRepository userRepo, PasswordEncoder encoder, RoutineService routineService) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.routineService = routineService;
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
        wishlist.setUpdatedAt(LocalDateTime.now());
        user.setWishlist(wishlist);

        userRepo.save(user);

        CreateMakeupRoutineRequestDTO casualRoutine = new CreateMakeupRoutineRequestDTO(
            OccasionEnum.CASUAL,
            null,
            "A simple and natural makeup routine for everyday wear."
        );

        CreateMakeupRoutineRequestDTO glamRoutine = new CreateMakeupRoutineRequestDTO(
            OccasionEnum.GLAM,
            null,
            "An elevated makeup routine for special occasions."
        );

        routineService.createMakeupRoutine(email, casualRoutine);
        routineService.createMakeupRoutine(email, glamRoutine);
    }

    public boolean login(String email, String rawPassword) {
        email = email.trim().toLowerCase();
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        return encoder.matches(rawPassword, user.getPasswordHash());
    }
}
