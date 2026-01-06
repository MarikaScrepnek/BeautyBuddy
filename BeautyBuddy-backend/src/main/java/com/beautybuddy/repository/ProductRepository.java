package com.beautybuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beautybuddy.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {}