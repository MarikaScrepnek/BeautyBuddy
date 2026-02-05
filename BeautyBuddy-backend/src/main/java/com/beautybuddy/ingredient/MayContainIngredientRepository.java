package com.beautybuddy.ingredient;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.product.Product;

public interface MayContainIngredientRepository extends JpaRepository<MayContainIngredient, Integer> {
    boolean existsByProduct(Product product);
}
