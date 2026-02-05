package com.beautybuddy.ingredient;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.product.Product;

public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, Integer> {
    boolean existsByProduct(Product product);
}