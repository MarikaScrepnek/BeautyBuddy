package com.beautybuddy.brand;

import com.beautybuddy.common.DTOMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandRepository brandRepository;

    public BrandController(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @GetMapping
    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(DTOMapper::toBrandDTO)
                .toList();
    }
}