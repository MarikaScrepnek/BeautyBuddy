package com.beautybuddy.controller;

import java.util.List;

import com.beautybuddy.model.Product;
import com.beautybuddy.repository.ProductRepository;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("http://localhost:5173")
public class ProductController {
  private final ProductRepository productRepository;
  private final ProductService productService;

  public ProductController(ProductRepository productRepository, ProductService productService) {
    this.productRepository = productRepository;
    this.productService = productService;
  }

  @GetMapping
  public List<Product> getAll() {
    return productRepository.findAll();
  }

  @GetMapping("/search")
  public List<Product> searchProducts(@RequestParam("q") String query) {
    return productRepository.searchByProductOrBrand(query);
  }

  @PostMapping
  public Product addProduct(@RequestBody ProductCreateRequest request) {
    return productService.createProduct(request);
  }
}
