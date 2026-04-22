package com.beautybuddy.ingredient;

import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class IngredientParser implements CommandLineRunner {

    private final ProductIngredientService productIngredientService;
    private final MayContainIngredientService mayContainIngredientService;

    public IngredientParser(ProductIngredientService productIngredientService,
                       MayContainIngredientService mayContainIngredientService) {
        this.productIngredientService = productIngredientService;
        this.mayContainIngredientService = mayContainIngredientService;
    }

    @Override
    public void run(String... args) {
        if (Arrays.asList(args).contains("parse")) {
            System.out.println("Starting ingredient parsing...");

            productIngredientService.parseIngredientsForAllProducts();
            mayContainIngredientService.parseIngredientsForAllProducts();

            System.out.println("Done parsing.");
            System.exit(0);
        }
    }
}
