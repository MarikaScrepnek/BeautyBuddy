package com.beautybuddy.service;

import com.beautybuddy.model.Ingredient;
import com.beautybuddy.model.Product;
import com.beautybuddy.model.ProductIngredient;
import com.beautybuddy.repository.IngredientRepository;
import com.beautybuddy.repository.ProductIngredientRepository;
import com.beautybuddy.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.HashSet;
import java.util.Set;

@Service
public class ProductIngredientService {

    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;
    private final ProductIngredientRepository productIngredientRepository;

    public ProductIngredientService(ProductRepository productRepository,
                                    IngredientRepository ingredientRepository,
                                    ProductIngredientRepository productIngredientRepository) {
        this.productRepository = productRepository;
        this.ingredientRepository = ingredientRepository;
        this.productIngredientRepository = productIngredientRepository;
    }

    // Return the canonical Ingredient object directly
    public Ingredient getCanonicalIngredient(String name) {
        Ingredient ingredient = ingredientRepository.findByName(name)
            .orElseGet(() -> {
                Ingredient newIng = new Ingredient();
                newIng.setName(name);
                newIng.setCanonicalId(null);
                return ingredientRepository.save(newIng);
            });

        // If it has a canonicalId, fetch the canonical ingredient object
        Integer canonicalIdValue = ingredient.getCanonicalId();
        if (canonicalIdValue != null) {
            ingredient = ingredientRepository.findById(canonicalIdValue)
                                           .orElse(ingredient);
        }

        return ingredient;
    }

    @Transactional
    public void parseIngredientsForAllProducts() {
        for (Product product : productRepository.findAll()) {
            if (productIngredientRepository.existsByProduct(product)) {
                continue;
            }

            String raw = product.getRawIngredients();
            if (raw == null || raw.isEmpty()) continue;

            raw = raw.replaceAll("\\([^)]*\\)", ""); // remove parentheses
            raw = raw.replace("/", ",");             // replace slashes
            String[] ingredients = raw.split(",");

            Set<Integer> addedCanonicals = new HashSet<>();
            int order = 1;

            for (String ing : ingredients) {
                String normalized = ing.trim().toLowerCase(Locale.ROOT);
                if (normalized.isEmpty()) continue;

                // get canonical Ingredient directly
                Ingredient canonicalIngredient = getCanonicalIngredient(normalized);
                int canonicalId = canonicalIngredient.getIngredient_id();

                if (!addedCanonicals.contains(canonicalId)) {
                    ProductIngredient pi = new ProductIngredient();
                    pi.setProduct(product);
                    pi.setIngredient(canonicalIngredient);
                    pi.setPosition(order++);
                    addedCanonicals.add(canonicalId);

                    productIngredientRepository.save(pi);
                }
            }
        }
    }
}
