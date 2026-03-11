package com.beautybuddy.ingredient.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.ingredient.entity.MayContainIngredient;
import com.beautybuddy.product.Product;

public interface MayContainIngredientRepository extends JpaRepository<MayContainIngredient, Integer> {
    boolean existsByProduct(Product product);
}
