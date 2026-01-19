package com.beautybuddy.controller;

import com.beautybuddy.service.IngredientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/parse")
    public String parseIngredients() {
        ingredientService.parseIngredientsForAllProducts();
        return "Ingredients parsed!";
    }
}
