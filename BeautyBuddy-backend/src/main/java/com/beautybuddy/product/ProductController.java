package com.beautybuddy.product;

import java.util.List;

import com.beautybuddy.common.DTOMapper;
import com.beautybuddy.ingredient.dto.MayContainIngredientDTO;
import com.beautybuddy.ingredient.dto.ProductIngredientDTO;
import com.beautybuddy.product.dto.ProductDTO;
import com.beautybuddy.product.entity.Product;
import com.beautybuddy.product.repo.ProductRepository;
import com.beautybuddy.report.ReportService;
import com.beautybuddy.report.ReportRequestDTO;
import com.beautybuddy.security.CustomUserDetails;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ReportService reportService;

    public ProductController(ProductRepository productRepository, ReportService reportService) {
        this.productRepository = productRepository;
        this.reportService = reportService;
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
    public ProductDTO getProduct(@PathVariable("id") Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return DTOMapper.toProductDTO(product);
    }

    @GetMapping("/{id}/ingredients")
    public List<ProductIngredientDTO> getProductIngredients(@PathVariable("id") Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return product.getProductIngredients().stream()
                .map(DTOMapper::toProductIngredientDTO)
                .toList();
    }

    @GetMapping("/{id}/maycontain")
    public List<MayContainIngredientDTO> getMayContainIngredients(@PathVariable("id") Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return product.getMayContainIngredients().stream()
                .map(DTOMapper::toMayContainIngredientDTO)
                .toList();
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<Void> report(@PathVariable("id") Long productId, @RequestBody ReportRequestDTO reportDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        reportService.report(userDetails.getEmail(), reportDTO.reason(), "product", productId);
        return ResponseEntity.ok().build();
    }
}
