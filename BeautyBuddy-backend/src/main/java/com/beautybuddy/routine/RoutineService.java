package com.beautybuddy.routine;

import java.util.List;

import org.springframework.stereotype.Service;

import com.beautybuddy.category.Category;
import com.beautybuddy.category.CategoryRepository;
import com.beautybuddy.product.Product;
import com.beautybuddy.product.ProductRepository;
import com.beautybuddy.product.ProductShade;
import com.beautybuddy.product.ProductShadeRepository;
import com.beautybuddy.routine.dto.AddToRoutineRequestDTO;
import com.beautybuddy.routine.dto.CreateMakeupRoutineRequestDTO;
import com.beautybuddy.routine.dto.DisplayRoutineDTO;
import com.beautybuddy.routine.dto.DisplayRoutineItemDTO;
import com.beautybuddy.routine.entity.MakeupRoutine;
import com.beautybuddy.routine.entity.MakeupRoutineItem;
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
    private final ProductRepository productRepository;
    private final ProductShadeRepository productShadeRepository;

    public RoutineService(UserRepository userRepository, CategoryRepository categoryRepository, RoutineRepository routineRepository, MakeupRoutineRepository makeupRoutineRepository, ReviewRepository reviewRepository, ProductRepository productRepository, ProductShadeRepository productShadeRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.routineRepository = routineRepository;
        this.makeupRoutineRepository = makeupRoutineRepository;
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.productShadeRepository = productShadeRepository;
    }

    public List<DisplayRoutineDTO> getMakeupRoutines(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<MakeupRoutine> routines = makeupRoutineRepository.findByUser(user);
        return routines.stream()
            .map(r -> new DisplayRoutineDTO(r.getRoutine().getId(), r.getName(), user.getUsername(), r.getRoutine().getUpdatedAt(), r.getOccasion().name(), r.getRoutine().getNotes(), r.getItems().stream()
                .map(i -> new DisplayRoutineItemDTO(
                    i.getId(),
                    i.getProduct().getId(),
                    i.getProduct().getName(),
                    i.getProduct().getBrand().getName(),
                    i.getShade().getShadeName(),
                    i.getProduct().getCategory().getName(),
                    i.getShade() != null && i.getShade().getImageLink() != null
                        ? i.getShade().getImageLink()
                        : i.getProduct().getImageLink(),
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

    public void addProductToRoutine(String userEmail, Long routineId, AddToRoutineRequestDTO request) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Routine routine = routineRepository.findById(routineId)
            .orElseThrow(() -> new RuntimeException("Routine not found"));
        Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        ProductShade shade = null;
        if (request.shadeName() != null) {
            shade = productShadeRepository.findByProductAndShadeName(product, request.shadeName())
                .orElseThrow(() -> new RuntimeException("Shade not found"));
        }

        if (routine.getCategory().getName().equals("Makeup")) {
            MakeupRoutine makeupRoutine = makeupRoutineRepository.findByRoutineIdAndUserEmail(routineId, userEmail)
                .orElseThrow(() -> new RuntimeException("Makeup routine not found"));
            if (!makeupRoutine.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized");
            }
             
            MakeupRoutineItem item = new MakeupRoutineItem();
            item.setMakeupRoutine(makeupRoutine);
            item.setProduct(product);
            item.setShade(shade);

            makeupRoutine.getItems().add(item);
            makeupRoutineRepository.save(makeupRoutine);

        } else if (routine.getCategory().getName().equals("Skincare")) {
            throw new RuntimeException("Unsupported routine category");
        } else if (routine.getCategory().getName().equals("Haircare")) {
            throw new RuntimeException("Unsupported routine category");
        } else {
            throw new RuntimeException("Unknown routine category");
        }
    }
}
