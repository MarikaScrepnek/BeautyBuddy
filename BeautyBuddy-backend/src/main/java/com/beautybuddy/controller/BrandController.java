package com.beautybuddy.controller;

import com.beautybuddy.model.Brand;
import com.beautybuddy.repository.BrandRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:5173")
public class BrandController {

    private final BrandRepository brandRepository;

    public BrandController(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @GetMapping("/api/brands")
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }
}