package com.beautybuddy.service;

import com.beautybuddy.model.Ingredient;
import com.beautybuddy.model.Product;
import com.beautybuddy.model.ProductIngredient;
import com.beautybuddy.repository.IngredientRepository;
import com.beautybuddy.repository.ProductIngredientRepository;
import com.beautybuddy.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Locale;

@Service
public class IngredientService {

    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;
    private final ProductIngredientRepository productIngredientRepository;

    public IngredientService(ProductRepository productRepository,
                             IngredientRepository ingredientRepository,
                             ProductIngredientRepository productIngredientRepository) {
        this.productRepository = productRepository;
        this.ingredientRepository = ingredientRepository;
        this.productIngredientRepository = productIngredientRepository;
    }

    @Transactional
    public void parseIngredientsForAllProducts() {
        // get all products with raw ingredients
        for (Product product : productRepository.findAll()) {
            String raw = product.getRawIngredients();
            if (raw == null || raw.isEmpty()) continue;

            String[] ingredients = raw.split(","); // split by comma
            for (String ing : ingredients) {
                String normalized = ing.trim().toLowerCase(Locale.ROOT);

                // check if ingredient already exists
                Ingredient ingredient = ingredientRepository.findByNormalizedName(normalized)
                        .orElseGet(() -> {
                            Ingredient newIng = new Ingredient();
                            newIng.setName(normalized); // use normalized as name
                            newIng.setNormalizedName(normalized);
                            return ingredientRepository.save(newIng);
                        });

                // create join if not exists
                boolean exists = productIngredientRepository.existsByProductAndIngredient(product, ingredient);
                if (!exists) {
                    ProductIngredient pi = new ProductIngredient();
                    pi.setProduct(product);
                    pi.setIngredient(ingredient);
                    productIngredientRepository.save(pi);
                }
            }

            // optionally clear raw_ingredients
            product.setRawIngredients(null);
            productRepository.save(product);
        }
    }
}
