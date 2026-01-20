package com.beautybuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.model.ProductIngredient;
import com.beautybuddy.model.Product;

public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, Integer> {
    boolean existsByProduct(Product product);
}