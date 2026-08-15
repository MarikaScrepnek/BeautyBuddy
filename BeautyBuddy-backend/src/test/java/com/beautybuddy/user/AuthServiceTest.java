package com.beautybuddy.user;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.beautybuddy.breakout.repo.BreakoutListRepository;
import com.beautybuddy.category.CategoryRepository;
import com.beautybuddy.routine.RoutineService;
import com.beautybuddy.routine.repo.RoutineRepository;
import com.beautybuddy.user.entity.User;
import com.beautybuddy.user.repo.UserRepository;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

class AuthServiceTest {

    @Test
    void updateProfile_ignoresBlankEnumValues() {
        UserRepository userRepo = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        RoutineService routineService = mock(RoutineService.class);
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        RoutineRepository routineRepository = mock(RoutineRepository.class);
        BreakoutListRepository breakoutListRepo = mock(BreakoutListRepository.class);

        AuthService authService = new AuthService(
                userRepo,
                encoder,
                routineService,
                categoryRepository,
                routineRepository,
                breakoutListRepo,
                new SimpleMeterRegistry()
        );

        User user = new User();
        when(userRepo.findByEmail("me@example.com")).thenReturn(Optional.of(user));

        Map<String, String> updates = Map.of(
                "pronouns", "",
                "skintype", "",
                "skinconcerns", "",
                "hairtype", "",
                "hairdensity", ""
        );

        assertDoesNotThrow(() -> authService.updateProfile("username", "me@example.com", updates));
        assertNull(user.getPronouns());
        assertNull(user.getSkinType());
        assertNull(user.getSkinConcerns());
        verify(userRepo).save(user);
    }
}
