package com.beautybuddy.repository;

import com.beautybuddy.model.Product;
import com.beautybuddy.model.ProductShade;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShadeRepository extends JpaRepository<ProductShade, Integer> {
    Optional<ProductShade> findByProductAndName(Product product, String shadeName);
}
