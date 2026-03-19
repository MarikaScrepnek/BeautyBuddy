package com.beautybuddy.ingredient;

import com.beautybuddy.common.DTOMapper;
import com.beautybuddy.ingredient.dto.IngredientDTO;
import com.beautybuddy.ingredient.repo.IngredientRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public Page<IngredientDTO> getAllIngredients(Pageable pageable) {
        return ingredientRepository.findAll(pageable)
            .map(DTOMapper::toIngredientDTO);
    }

    // this should be changed before release as its not the most secure way to do this
    @GetMapping("/parse")
    public String parseIngredients() {
        productIngredientService.parseIngredientsForAllProducts();
        mayContainIngredientService.parseIngredientsForAllProducts();
        return "Ingredients parsed!";
    }
}
