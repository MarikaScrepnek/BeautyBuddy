package com.beautybuddy.ingredient.repo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.ingredient.entity.Ingredient;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByName(String name);
    Page<Ingredient> findByCanonicalIdNull(Pageable pageable);
    Page<Ingredient> findByCanonicalIdNullAndNameContaining(Pageable pageable, String name);
    Page<Ingredient> findByCanonicalIdNotNullAndNameContaining(Pageable pageable, String name);

    Page<Ingredient> findByCanonicalIdNullOrderByNameAsc(Pageable pageable);
    Page<Ingredient> findByCanonicalIdNullAndNameContainingIgnoreCaseOrderByNameAsc(String name, Pageable pageable);
    Page<Ingredient> findByCanonicalIdNotNullAndNameContainingIgnoreCaseOrderByNameAsc(String name, Pageable pageable);
}