package com.beautybuddy.service;

import com.beautybuddy.model.Ingredient;
import com.beautybuddy.model.MayContainIngredient;
import com.beautybuddy.model.Product;
import com.beautybuddy.repository.IngredientRepository;
import com.beautybuddy.repository.ProductRepository;
import com.beautybuddy.repository.MayContainIngredientRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import java.util.HashSet;
import java.util.Set;

@Service
public class MayContainIngredientService {

    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;
    private final MayContainIngredientRepository mayContainIngredientRepository;

    public MayContainIngredientService(ProductRepository productRepository,
                             IngredientRepository ingredientRepository,
                             MayContainIngredientRepository mayContainIngredientRepository) {
        this.productRepository = productRepository;
        this.ingredientRepository = ingredientRepository;
        this.mayContainIngredientRepository = mayContainIngredientRepository;
    }

    public Ingredient getCanonicalIngredient(String name) {
        // Find ingredient by name, or create a new one if it doesn't exist
        Ingredient ingredient = ingredientRepository.findByName(name)
            .orElseGet(() -> {
                Ingredient newIng = new Ingredient();
                newIng.setName(name);
                newIng.setCanonicalId(null);
                return ingredientRepository.save(newIng);
            });

        // Safely resolve canonical ingredient if canonicalId is not null
        Integer canonicalIdValue = ingredient.getCanonicalId();
        if (canonicalIdValue != null) {
            ingredient = ingredientRepository.findById(canonicalIdValue).orElse(ingredient);
        }

        return ingredient; // return either the canonical ingredient or itself
    }

    @Transactional
    public void parseIngredientsForAllProducts() {
        for (Product product : productRepository.findAll()) {
            String raw = product.getRawMayContainIngredients();
            if (raw == null || raw.isEmpty()) continue;

            // Clean up ingredient string
            raw = raw.replaceAll("\\([^)]*\\)", ""); // remove parentheses
            raw = raw.replace("/", ",");             // replace slashes
            String[] ingredients = raw.split(",");

            Set<Integer> addedCanonicals = new HashSet<>();

            for (String ing : ingredients) {
                String normalized = ing.trim().toLowerCase(Locale.ROOT);
                if (normalized.isEmpty()) continue;

                // Get or create ingredient
                Ingredient ingredient = getCanonicalIngredient(normalized);

                // Resolve canonical ingredient safely
                Integer canonicalIdValue = ingredient.getCanonicalId();
                Ingredient canonicalIngredient;
                if (canonicalIdValue != null) {
                    canonicalIngredient = ingredientRepository.findById(canonicalIdValue).orElse(ingredient);
                } else {
                    canonicalIngredient = ingredient;
                }

                // Determine which ID to use for tracking duplicates
                Integer canonicalIdToAdd = (canonicalIngredient.getCanonicalId() != null)
                        ? canonicalIngredient.getCanonicalId()
                        : Integer.valueOf(canonicalIngredient.getIngredient_id());

                // Skip if already added
                if (!addedCanonicals.contains(canonicalIdToAdd)) {
                    MayContainIngredient mci = new MayContainIngredient();
                    mci.setProduct(product);
                    mci.setIngredient(canonicalIngredient);

                    addedCanonicals.add(canonicalIdToAdd);
                    mayContainIngredientRepository.save(mci);
                }
            }
        }
    }
}