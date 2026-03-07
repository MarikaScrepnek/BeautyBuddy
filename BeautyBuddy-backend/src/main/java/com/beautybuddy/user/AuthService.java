package com.beautybuddy.user;

import com.beautybuddy.user.entity.User;
import com.beautybuddy.wishlist.entity.Wishlist;
import com.beautybuddy.category.Category;
import com.beautybuddy.category.CategoryRepository;
import com.beautybuddy.routine.OccasionEnum;
import com.beautybuddy.routine.RoutineService;
import com.beautybuddy.routine.entity.MakeupRoutine;
import com.beautybuddy.routine.entity.Routine;
import com.beautybuddy.routine.repo.MakeupRoutineRepository;
import com.beautybuddy.routine.repo.RoutineRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    private final CategoryRepository categoryRepository;
    private final RoutineRepository routineRepository;
    private final MakeupRoutineRepository makeupRoutineRepository;


    public AuthService(UserRepository userRepo, PasswordEncoder encoder, RoutineService routineService, CategoryRepository categoryRepository, RoutineRepository routineRepository, MakeupRoutineRepository makeupRoutineRepository) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.categoryRepository = categoryRepository;
        this.routineRepository = routineRepository;
        this.makeupRoutineRepository = makeupRoutineRepository;
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

        Category makeupCategory = categoryRepository.findByName("Makeup")
        .orElseThrow(() -> new RuntimeException("Can't find category"));

        Routine baseCasualRoutine  = new Routine();
        baseCasualRoutine.setCategory(makeupCategory);
        baseCasualRoutine.setNotes("A simple and natural makeup routine for everyday wear.");

        MakeupRoutine routine = new MakeupRoutine();
        routine.setRoutine(baseCasualRoutine);
        routine.setUser(user);
        routine.setOccasion(OccasionEnum.CASUAL);

        routineRepository.save(baseCasualRoutine);
        makeupRoutineRepository.save(routine);

        Routine baseGlamRoutine  = new Routine();
        baseGlamRoutine.setCategory(makeupCategory);
        baseGlamRoutine.setNotes("An elevated makeup routine for special occasions.");

        MakeupRoutine glamRoutine = new MakeupRoutine();
        glamRoutine.setRoutine(baseGlamRoutine);
        glamRoutine.setUser(user);
        glamRoutine.setOccasion(OccasionEnum.GLAM);

        routineRepository.save(baseGlamRoutine);
        makeupRoutineRepository.save(glamRoutine);
    }

    public boolean login(String email, String rawPassword) {
        email = email.trim().toLowerCase();
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        return encoder.matches(rawPassword, user.getPasswordHash());
    }
}
