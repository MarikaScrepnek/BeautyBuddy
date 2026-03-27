package com.beautybuddy.product.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.product.entity.Product;
import com.beautybuddy.product.entity.ProductShade;

public interface ProductShadeRepository extends JpaRepository<ProductShade, Integer> {
    Optional<ProductShade> findByProductAndShadeName(Product product, String shadeName);
    Optional<ProductShade> findByProductAndShadeNumber(Product product, int shadeNumber);
}
