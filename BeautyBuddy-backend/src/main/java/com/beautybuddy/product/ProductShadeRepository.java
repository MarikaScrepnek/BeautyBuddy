package com.beautybuddy.product;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductShadeRepository extends JpaRepository<ProductShade, Integer> {
    Optional<ProductShade> findByProductAndShadeName(Product product, String shadeName);
}
