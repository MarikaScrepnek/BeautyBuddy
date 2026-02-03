package com.beautybuddy.products;

import java.util.List;

import com.beautybuddy.dto.*;
import com.beautybuddy.repository.ProductRepository;
import com.beautybuddy.dto.DTOMapper;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(DTOMapper::toProductDTO)
                .toList();
    }

    @GetMapping("/search")
    public List<ProductDTO> searchProducts(@RequestParam("q") String query) {
        return productRepository.searchByProductOrBrand(query).stream()
                .map(DTOMapper::toProductDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable("id") int productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return DTOMapper.toProductDTO(product);
    }

    @GetMapping("/{id}/ingredients")
    public List<ProductIngredientDTO> getProductIngredients(@PathVariable("id") int productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return product.getProductIngredients().stream()
                .map(DTOMapper::toProductIngredientDTO)
                .toList();
    }

    @GetMapping("/{id}/maycontain")
    public List<MayContainIngredientDTO> getMayContainIngredients(@PathVariable("id") int productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return product.getMayContainIngredients().stream()
                .map(DTOMapper::toMayContainIngredientDTO)
                .toList();
    }
}
