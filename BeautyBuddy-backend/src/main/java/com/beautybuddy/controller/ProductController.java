package com.beautybuddy.controller;

import java.util.List;

import com.beautybuddy.model.Product;
import com.beautybuddy.repository.ProductRepository;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("http://localhost:5173")
public class ProductController {
    private final ProductRepository repo;

  public ProductController(ProductRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<Product> getAll() {
    return repo.findAll();
  }
}
