package com.beautybuddy.ingredient;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beautybuddy.ingredient.entity.Ingredient;
import com.beautybuddy.ingredient.entity.ProductIngredient;
import com.beautybuddy.ingredient.repo.IngredientRepository;
import com.beautybuddy.ingredient.repo.ProductIngredientRepository;
import com.beautybuddy.product.Product;
import com.beautybuddy.product.ProductRepository;

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
        Long canonicalIdValue = ingredient.getCanonicalId();
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

            Set<Long> addedCanonicals = new HashSet<>();
            int order = 1;

            for (String ing : ingredients) {
                String normalized = ing.trim().toLowerCase(Locale.ROOT);
                if (normalized.isEmpty()) continue;

                // get canonical Ingredient directly
                Ingredient canonicalIngredient = getCanonicalIngredient(normalized);
                Long canonicalId = canonicalIngredient.getId();

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
