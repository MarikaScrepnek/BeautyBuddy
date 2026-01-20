// this should be changed before release as its not the most secure way to do this

package com.beautybuddy.controller;

import com.beautybuddy.service.ProductIngredientService;
import com.beautybuddy.service.MayContainIngredientService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final ProductIngredientService productIngredientService;
    private final MayContainIngredientService mayContainIngredientService;

    public IngredientController(ProductIngredientService productIngredientService, MayContainIngredientService mayContainIngredientService) {
        this.productIngredientService = productIngredientService;
        this.mayContainIngredientService = mayContainIngredientService;
    }

    @GetMapping("/parse")
    public String parseIngredients() {
        productIngredientService.parseIngredientsForAllProducts();
        mayContainIngredientService.parseIngredientsForAllProducts();
        return "Ingredients parsed!";
    }
}
