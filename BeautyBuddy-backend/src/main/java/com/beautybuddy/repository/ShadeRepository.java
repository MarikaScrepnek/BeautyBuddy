package com.beautybuddy.repository;

import com.beautybuddy.products.Product;
import com.beautybuddy.products.ProductShade;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShadeRepository extends JpaRepository<ProductShade, Integer> {
    Optional<ProductShade> findByProductAndShadeName(Product product, String shadeName);
}
