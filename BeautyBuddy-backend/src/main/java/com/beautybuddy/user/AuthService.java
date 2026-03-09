package com.beautybuddy.user;

import com.beautybuddy.user.entity.User;
import com.beautybuddy.wishlist.entity.Wishlist;
import com.beautybuddy.category.Category;
import com.beautybuddy.category.CategoryRepository;
import com.beautybuddy.routine.RoutineService;
import com.beautybuddy.routine.entity.OccasionEnum;
import com.beautybuddy.routine.entity.TimeOfDayEnum;
import com.beautybuddy.routine.entity.Routine;
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


    public AuthService(UserRepository userRepo, PasswordEncoder encoder, RoutineService routineService, CategoryRepository categoryRepository, RoutineRepository routineRepository) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.categoryRepository = categoryRepository;
        this.routineRepository = routineRepository;
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

        Category skincareCategory = categoryRepository.findByName("Skincare")
        .orElseThrow(() -> new RuntimeException("Can't find category"));

        Category haircareCategory = categoryRepository.findByName("Haircare")
        .orElseThrow(() -> new RuntimeException("Can't find category"));

        Routine casualMakeupRoutine  = new Routine();
        casualMakeupRoutine.setUser(user);
        casualMakeupRoutine.setCategory(makeupCategory);
        casualMakeupRoutine.setNotes("A simple and natural makeup routine for everyday wear.");
        casualMakeupRoutine.setOccasion(OccasionEnum.CASUAL);
        casualMakeupRoutine.setIsSystem(true);
        routineRepository.save(casualMakeupRoutine);

        Routine baseGlamRoutine  = new Routine();
        baseGlamRoutine.setUser(user);
        baseGlamRoutine.setCategory(makeupCategory);
        baseGlamRoutine.setNotes("An elevated makeup routine for special occasions.");
        baseGlamRoutine.setOccasion(OccasionEnum.GLAM);
        baseGlamRoutine.setIsSystem(true);
        routineRepository.save(baseGlamRoutine);

        Routine amSkincareRoutine = new Routine();
        amSkincareRoutine.setUser(user);
        amSkincareRoutine.setCategory(skincareCategory);
        amSkincareRoutine.setNotes("My daily morning skincare routine.");
        amSkincareRoutine.setTimeOfDay(TimeOfDayEnum.AM);
        amSkincareRoutine.setIsSystem(true);
        routineRepository.save(amSkincareRoutine);

        Routine pmSkincareRoutine = new Routine();
        pmSkincareRoutine.setUser(user);
        pmSkincareRoutine.setCategory(skincareCategory);
        pmSkincareRoutine.setNotes("My nightly skincare routine.");
        pmSkincareRoutine.setTimeOfDay(TimeOfDayEnum.PM);
        pmSkincareRoutine.setIsSystem(true);
        routineRepository.save(pmSkincareRoutine);

        Routine haircareRoutine = new Routine();
        haircareRoutine.setUser(user);
        haircareRoutine.setCategory(haircareCategory);
        haircareRoutine.setNotes("My go-to haircare routine for healthy hair.");
        haircareRoutine.setIsSystem(true);
        routineRepository.save(haircareRoutine);
    }

    public boolean login(String email, String rawPassword) {
        email = email.trim().toLowerCase();
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        return encoder.matches(rawPassword, user.getPasswordHash());
    }
}
