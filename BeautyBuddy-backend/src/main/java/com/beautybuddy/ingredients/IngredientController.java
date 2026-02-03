package com.beautybuddy.ingredients;

import com.beautybuddy.common.DTOMapper;
import com.beautybuddy.repository.IngredientRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientRepository ingredientRepository;

    private final ProductIngredientService productIngredientService;
    private final MayContainIngredientService mayContainIngredientService;

    public IngredientController(IngredientRepository ingredientRepository, ProductIngredientService productIngredientService, MayContainIngredientService mayContainIngredientService) {
        this.ingredientRepository = ingredientRepository;
        this.productIngredientService = productIngredientService;
        this.mayContainIngredientService = mayContainIngredientService;
    }

    @GetMapping
    public List<IngredientDTO> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(DTOMapper::toIngredientDTO)
                .toList();
    }

    // this should be changed before release as its not the most secure way to do this
    @GetMapping("/parse")
    public String parseIngredients() {
        productIngredientService.parseIngredientsForAllProducts();
        mayContainIngredientService.parseIngredientsForAllProducts();
        return "Ingredients parsed!";
    }
}
