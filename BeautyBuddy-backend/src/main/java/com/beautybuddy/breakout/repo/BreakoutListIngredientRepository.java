package com.beautybuddy.breakout.repo;

public interface BreakoutListIngredientRepository extends org.springframework.data.jpa.repository.JpaRepository<com.beautybuddy.breakout.entity.BreakoutListIngredient, Long> {
    java.util.Optional<com.beautybuddy.breakout.entity.BreakoutListIngredient> findByBreakoutListIdAndIngredientId(Long breakoutListId, Long ingredientId);
}
