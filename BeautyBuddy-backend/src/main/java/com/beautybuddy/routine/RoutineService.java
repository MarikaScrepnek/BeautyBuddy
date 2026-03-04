package com.beautybuddy.routine;

import java.util.List;

import org.springframework.stereotype.Service;

import com.beautybuddy.category.Category;
import com.beautybuddy.category.CategoryRepository;
import com.beautybuddy.routine.dto.CreateMakeupRoutineRequestDTO;
import com.beautybuddy.routine.dto.DisplayRoutineDTO;
import com.beautybuddy.routine.dto.DisplayRoutineItemDTO;
import com.beautybuddy.routine.entity.MakeupRoutine;
import com.beautybuddy.routine.entity.Routine;
import com.beautybuddy.routine.repo.MakeupRoutineRepository;
import com.beautybuddy.routine.repo.RoutineRepository;
import com.beautybuddy.user.UserRepository;
import com.beautybuddy.user.entity.User;
import com.beautybuddy.review.ReviewRepository;
import com.beautybuddy.review.entity.Review;

@Service
public class RoutineService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RoutineRepository routineRepository;
    private final MakeupRoutineRepository makeupRoutineRepository;
    private final ReviewRepository reviewRepository;

    public RoutineService(UserRepository userRepository, CategoryRepository categoryRepository, RoutineRepository routineRepository, MakeupRoutineRepository makeupRoutineRepository, ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.routineRepository = routineRepository;
        this.makeupRoutineRepository = makeupRoutineRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<DisplayRoutineDTO> getMakeupRoutines(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<MakeupRoutine> routines = makeupRoutineRepository.findByUser(user);
        return routines.stream()
            .map(r -> new DisplayRoutineDTO(r.getName(), user.getUsername(), r.getRoutine().getUpdatedAt(), r.getOccasion().name(), r.getRoutine().getNotes(), r.getItems().stream()
                .map(i -> new DisplayRoutineItemDTO(
                    i.getProduct().getId(),
                    i.getProduct().getName(),
                    i.getProduct().getBrand().getName(),
                    i.getShade().getShadeName(),
                    i.getProduct().getCategory().getName(),
                    i.getProduct().getImageLink(),
                    i.getNotes(),
                    reviewRepository.findByProduct_IdAndProductShade_IdAndUser_Id(
                        i.getProduct().getId(),
                        i.getShade().getId(),
                        user.getId()
                    ).map(Review::getRating).orElse(null)
                ))
                .toList()
            ))
            .toList();
    }

    public void createMakeupRoutine(String userEmail, CreateMakeupRoutineRequestDTO request) {
        Category category = categoryRepository.findByName("Makeup")
            .orElseThrow(() -> new RuntimeException("Category not found"));

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Routine baseRoutine  = new Routine();
        baseRoutine.setCategory(category);
        baseRoutine.setNotes(request.notes());

        MakeupRoutine routine = new MakeupRoutine();
        routine.setRoutine(baseRoutine);
        routine.setUser(user);
        routine.setOccasion(request.occasion());
        routine.setName(request.name());

        routineRepository.save(baseRoutine);
        makeupRoutineRepository.save(routine);
    }
}
