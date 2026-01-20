package com.beautybuddy.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.beautybuddy.dto.MayContainIngredientDTO;
import com.beautybuddy.dto.ProductIngredientDTO;
import com.beautybuddy.model.Product;
import com.beautybuddy.repository.ProductRepository;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("http://localhost:5173")
public class ProductController {
  private final ProductRepository productRepository;

  public ProductController(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @GetMapping
  public List<Product> getAll() {
    return productRepository.findAll();
  }

  @GetMapping("/search")
  public List<Product> searchProducts(@RequestParam("q") String query) {
    return productRepository.searchByProductOrBrand(query);
  }

  @GetMapping("/{product_id}/ingredients")
  public List<ProductIngredientDTO> getIngredients(@PathVariable int product_id) {
    Product product = productRepository.findById(product_id)
        .orElseThrow(() -> new RuntimeException("Product not found"));

      return product.getProductIngredients().stream()
              .map(ProductIngredient -> new ProductIngredientDTO(
                      ProductIngredient.getIngredient().getIngredient_id(),
                      ProductIngredient.getIngredient().getName(),
                      ProductIngredient.getIngredient().getCanonicalId()
              ))
              .collect(Collectors.toList());
  }

  @GetMapping("/{product_id}/maycontain")
    public List<MayContainIngredientDTO> getMayContainIngredients(@PathVariable int product_id) {
        Product product = productRepository.findById(product_id)
          .orElseThrow(() -> new RuntimeException("Product not found"));

        return product.getMayContainIngredients().stream()
                .map(MayContainIngredient -> new MayContainIngredientDTO(
                        MayContainIngredient.getIngredient().getIngredient_id(),
                        MayContainIngredient.getIngredient().getName(),
                        MayContainIngredient.getIngredient().getCanonicalId()
                ))
                .collect(Collectors.toList());
  }
}
