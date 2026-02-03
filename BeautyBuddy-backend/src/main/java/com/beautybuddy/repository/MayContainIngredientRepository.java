package com.beautybuddy.repository;

import com.beautybuddy.model.MayContainIngredient;
import com.beautybuddy.products.Product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MayContainIngredientRepository extends JpaRepository<MayContainIngredient, Integer> {
    boolean existsByProduct(Product product);
}
