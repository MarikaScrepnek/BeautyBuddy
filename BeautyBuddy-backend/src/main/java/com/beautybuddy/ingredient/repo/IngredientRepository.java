package com.beautybuddy.ingredient.repo;
import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.ingredient.entity.Ingredient;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByName(String name);
}