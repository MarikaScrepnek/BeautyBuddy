package com.beautybuddy.routine;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.beautybuddy.category.Category;
import com.beautybuddy.category.CategoryRepository;
import com.beautybuddy.common.DTOMapper;
import com.beautybuddy.product.Product;
import com.beautybuddy.product.ProductRepository;
import com.beautybuddy.product.ProductShade;
import com.beautybuddy.product.ProductShadeRepository;
import com.beautybuddy.routine.dto.AddToRoutineRequestDTO;
import com.beautybuddy.routine.dto.CreateMakeupRoutineRequestDTO;
import com.beautybuddy.routine.dto.DisplayRoutineDTO;
import com.beautybuddy.routine.dto.DisplayRoutineItemDTO;
import com.beautybuddy.routine.entity.OccasionEnum;
import com.beautybuddy.routine.entity.Routine;
import com.beautybuddy.routine.entity.RoutineItem;
import com.beautybuddy.routine.repo.RoutineRepository;
import com.beautybuddy.user.UserRepository;
import com.beautybuddy.user.entity.User;
import com.beautybuddy.review.ReviewRepository;

@Service
public class RoutineService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RoutineRepository routineRepository;
    private final ProductRepository productRepository;
    private final ProductShadeRepository productShadeRepository;
    private final ReviewRepository reviewRepository;

    public RoutineService(UserRepository userRepository, CategoryRepository categoryRepository, RoutineRepository routineRepository, ReviewRepository reviewRepository, ProductRepository productRepository, ProductShadeRepository productShadeRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.routineRepository = routineRepository;
        this.productRepository = productRepository;
        this.productShadeRepository = productShadeRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<DisplayRoutineDTO> getMakeupRoutines(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<Routine> routines = routineRepository.findByUserIdAndCategoryName(user.getId(), "Makeup");

        return routines.stream()
            .map(routine -> DTOMapper.toDisplayRoutineDTO(routine, reviewRepository))
            .toList();
    }

    public void createMakeupRoutine(String userEmail, CreateMakeupRoutineRequestDTO request) {
        Category category = categoryRepository.findByName("Makeup")
            .orElseThrow(() -> new RuntimeException("Category not found"));

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.occasion() == OccasionEnum.CASUAL || request.occasion() == OccasionEnum.GLAM) {
            throw new RuntimeException("Cannot create routine for this occasion");
        }
        
        Routine routine  = new Routine();
        routine.setUser(user);
        routine.setCategory(category);
        routine.setName(request.name());
        routine.setNotes(request.notes());
        routine.setOccasion(request.occasion());

        routineRepository.save(routine);
    }

    public void addProductToRoutine(String userEmail, Long routineId, AddToRoutineRequestDTO request) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        ProductShade shade = null;
        if (request.shadeName() != null && !request.shadeName().isEmpty()) {
            shade = productShadeRepository.findByProductAndShadeName(product, request.shadeName())
                .orElseThrow(() -> new RuntimeException("Shade not found"));
        }

        Routine routine = routineRepository.findByIdAndUserEmail(routineId, userEmail)
            .orElseThrow(() -> new RuntimeException("Routine not found"));
        if (!routine.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        Integer stepOrder = routine.getItems().size() + 1;
            
        RoutineItem item = new RoutineItem();
        item.setRoutine(routine);
        item.setProduct(product);
        item.setShade(shade);
        item.setStepOrder(stepOrder);

        routine.getItems().add(item);
        routineRepository.save(routine);
    }

    public DisplayRoutineDTO updateRoutine(String userEmail, DisplayRoutineDTO request) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Routine routine = routineRepository.findById(request.routineId())
            .orElseThrow(() -> new RuntimeException("Routine not found"));
        
        if (!routine.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        LocalDateTime now = LocalDateTime.now();
        routine.getItems().forEach(item -> {
            if (item.getValidTo() == null) {
                item.setValidTo(now);
            }
        });

        int stepOrder = 1;
        for (DisplayRoutineItemDTO itemDTO : request.items()) {
            Product product = productRepository.findById(itemDTO.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
            ProductShade shade = null;
            if (itemDTO.productShadeName() != null) {
                shade = productShadeRepository.findByProductAndShadeName(product, itemDTO.productShadeName())
                    .orElseThrow(() -> new RuntimeException("Shade not found"));
            }

            RoutineItem newItem = new RoutineItem();
            newItem.setRoutine(routine);
            newItem.setProduct(product);
            newItem.setShade(shade);
            newItem.setNotes(itemDTO.productNotes());
            newItem.setStepOrder(stepOrder++);
            newItem.setValidFrom(now);

            routine.getItems().add(newItem);
        }

        routine.setNotes(request.notes());
        routine.setUpdatedAt(now);
        routineRepository.save(routine);
        return DTOMapper.toDisplayRoutineDTO(routine, reviewRepository);
    }
    
}
