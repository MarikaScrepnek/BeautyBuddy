package com.beautybuddy.ingredient.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.ingredient.entity.ProductIngredient;
import com.beautybuddy.product.Product;

public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, Integer> {
    boolean existsByProduct(Product product);
}