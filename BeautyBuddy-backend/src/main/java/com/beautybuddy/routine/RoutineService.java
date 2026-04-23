package com.beautybuddy.routine;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.beautybuddy.category.Category;
import com.beautybuddy.category.CategoryRepository;
import com.beautybuddy.common.DTOMapper;
import com.beautybuddy.config.RedisCacheConfig;
import com.beautybuddy.product.entity.Product;
import com.beautybuddy.product.entity.ProductShade;
import com.beautybuddy.product.repo.ProductRepository;
import com.beautybuddy.product.repo.ProductShadeRepository;
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

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class RoutineService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RoutineRepository routineRepository;
    private final ProductRepository productRepository;
    private final ProductShadeRepository productShadeRepository;
    private final ReviewRepository reviewRepository;

    private final Counter routineCreationCounter;
    private final Counter routineUpdateCounter;
    private final Counter routineAddProductCounter;
    private final Counter routineRemoveProductCounter;

    public RoutineService(UserRepository userRepository, CategoryRepository categoryRepository, RoutineRepository routineRepository, ReviewRepository reviewRepository, ProductRepository productRepository, ProductShadeRepository productShadeRepository, MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.routineRepository = routineRepository;
        this.productRepository = productRepository;
        this.productShadeRepository = productShadeRepository;
        this.reviewRepository = reviewRepository;
        this.routineCreationCounter = Counter.builder("routine_creation_total")
            .description("Total number of routines created")
            .register(meterRegistry);
        this.routineUpdateCounter = Counter.builder("routine_update_total")
            .description("Total number of routines updated")
            .register(meterRegistry);
        this.routineAddProductCounter = Counter.builder("routine_add_product_total")
            .description("Total number of products added to routines")
            .register(meterRegistry);
        this.routineRemoveProductCounter = Counter.builder("routine_remove_product_total")
            .description("Total number of products removed from routines")
            .register(meterRegistry);
    }

    @Cacheable(cacheNames = RedisCacheConfig.ROUTINE_CACHE, key = "#userEmail + ':makeup'")
    public List<DisplayRoutineDTO> getMakeupRoutines(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<Routine> routines = routineRepository.findByUserIdAndCategoryName(user.getId(), "Makeup");

        List<String> occasionOrder = List.of("CASUAL", "GLAM", "EVENT", "OTHER");
        routines.sort((a, b) -> {
            String occA = a.getOccasion() != null ? a.getOccasion().name() : "OTHER";
            String occB = b.getOccasion() != null ? b.getOccasion().name() : "OTHER";
            int idxA = occasionOrder.indexOf(occA);
            int idxB = occasionOrder.indexOf(occB);
            return Integer.compare(idxA == -1 ? occasionOrder.size() : idxA, idxB == -1 ? occasionOrder.size() : idxB);
        });

        return routines.stream()
            .map(routine -> DTOMapper.toDisplayRoutineDTO(routine, reviewRepository))
            .toList();
    }

    @Cacheable(cacheNames = RedisCacheConfig.ROUTINE_CACHE, key = "#userEmail + ':skincare'")
    public List<DisplayRoutineDTO> getSkincareRoutines(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<Routine> routines = routineRepository.findByUserIdAndCategoryName(user.getId(), "Skincare");

        routines.sort((a, b) -> {
            String timeA = a.getTimeOfDay() != null ? a.getTimeOfDay().name() : "PM";
            String timeB = b.getTimeOfDay() != null ? b.getTimeOfDay().name() : "PM";
            int idxA = "AM".equals(timeA) ? 0 : 1;
            int idxB = "AM".equals(timeB) ? 0 : 1;
            return Integer.compare(idxA, idxB);
        });

        return routines.stream()
            .map(routine -> DTOMapper.toDisplayRoutineDTO(routine, reviewRepository))
            .toList();
    }

    @Cacheable(cacheNames = RedisCacheConfig.ROUTINE_CACHE, key = "#userEmail + ':haircare'")
    public DisplayRoutineDTO getHaircareRoutine(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<Routine> routines = routineRepository.findByUserIdAndCategoryName(user.getId(), "Haircare");

        if (routines.isEmpty()) {
            throw new RuntimeException("No haircare routine found");
        }

        return DTOMapper.toDisplayRoutineDTO(routines.get(0), reviewRepository);
    }

    @CacheEvict(cacheNames = RedisCacheConfig.ROUTINE_CACHE, allEntries = true)
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
        routineCreationCounter.increment();
    }

    @CacheEvict(cacheNames = RedisCacheConfig.ROUTINE_CACHE, allEntries = true)
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
        if (!product.getProductShades().isEmpty() && shade == null) {
            shade = productShadeRepository.findByProductAndShadeNumber(product, 1)
                .orElseThrow(() -> new RuntimeException("Default shade not found"));
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
        routineAddProductCounter.increment();
    }

    @CacheEvict(cacheNames = RedisCacheConfig.ROUTINE_CACHE, allEntries = true)
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

            if (itemDTO.productNotes() != null && itemDTO.productNotes().length() > 126) {
                throw new RuntimeException("Product notes cannot exceed 126 characters");
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

        if (request.name() != null && request.name().length() > 128) {
            throw new RuntimeException("Routine name cannot exceed 128 characters");
        }
        routine.setName(request.name());

        if (request.notes() != null && request.notes().length() > 400) {
            throw new RuntimeException("Routine notes cannot exceed 400 characters");
        }
        routine.setNotes(request.notes());
        routine.setUpdatedAt(now);
        routineRepository.save(routine);
        routineUpdateCounter.increment();
        return DTOMapper.toDisplayRoutineDTO(routine, reviewRepository);
    }

    @Cacheable(cacheNames = RedisCacheConfig.ROUTINE_CACHE, key = "#userEmail + ':items'")
    public List<Long> getAllRoutineItems(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<Routine> routines = routineRepository.findByUserId(user.getId());

        return routines.stream()
            .flatMap(routine -> routine.getItems().stream())
            .filter(item -> item.getValidTo() == null)
            .map(item -> item.getProduct().getId())
            .toList();
    }

    @CacheEvict(cacheNames = RedisCacheConfig.ROUTINE_CACHE, allEntries = true)
    public void removeProductFromRoutine(String userEmail, Long routineId, Long productId, String shadeName) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Routine routine = routineRepository.findByIdAndUserEmail(routineId, userEmail)
            .orElseThrow(() -> new RuntimeException("Routine not found"));

        if (!routine.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        String normalizedShadeName = shadeName;
        if (normalizedShadeName != null) {
            normalizedShadeName = normalizedShadeName.trim();
            if ((normalizedShadeName.startsWith("\"") && normalizedShadeName.endsWith("\""))
                    || (normalizedShadeName.startsWith("'") && normalizedShadeName.endsWith("'"))) {
                normalizedShadeName = normalizedShadeName.substring(1, normalizedShadeName.length() - 1);
            }
            if (normalizedShadeName.isBlank() || "null".equalsIgnoreCase(normalizedShadeName)) {
                normalizedShadeName = null;
            }
        }
        final String shadeNameForMatch = normalizedShadeName;

        RoutineItem itemToRemove = routine.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(productId)
                && item.getValidTo() == null
                && (shadeNameForMatch == null || (item.getShade() != null && shadeNameForMatch.equals(item.getShade().getShadeName()))))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Product not found in routine"));

        itemToRemove.setValidTo(LocalDateTime.now());
        routineRepository.save(routine);
        routineRemoveProductCounter.increment();
    }
    
}
