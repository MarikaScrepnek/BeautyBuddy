package com.beautybuddy.category;

import com.beautybuddy.common.DTOMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/api/categories")
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(DTOMapper::toCategoryDTO)
                .toList();
    }
}