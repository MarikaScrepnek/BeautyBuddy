package com.beautybuddy.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.beautybuddy.model.Ingredient;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    Optional<Ingredient> findByName(String name);
}