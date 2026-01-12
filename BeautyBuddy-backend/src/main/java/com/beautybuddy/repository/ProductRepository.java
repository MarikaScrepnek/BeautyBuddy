package com.beautybuddy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beautybuddy.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByNameContainingIgnoreCase(String name);
}