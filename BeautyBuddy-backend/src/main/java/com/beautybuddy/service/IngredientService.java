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

    public Ingredient getCanonicalIngredient(String name) {
        Ingredient ingredient = ingredientRepository.findByName(name) //find ingredient in ingredient table
            .orElseGet(() -> { //if not found, create a new ingredient and return it
                Ingredient newIng = new Ingredient();
                newIng.setName(name);
                newIng.setCanonicalId(null);
                return ingredientRepository.save(newIng);
            });;

        if (ingredient.getCanonicalId() != null) { //if ingredient has a canonical id, return the canonical ingredient
            ingredient = ingredientRepository.findById(ingredient.getCanonicalId()).orElse(ingredient);
        }
        return ingredient; //else return the ingredient itself
    }

    @Transactional
    public void parseIngredientsForAllProducts() {
        for (Product product : productRepository.findAll()) {
            String raw = product.getRawIngredients();
            if (raw == null || raw.isEmpty()) continue;
            
            raw = raw.replaceAll("\\([^)]*\\)", ""); //remove parentheses and its conent
            raw = raw.replace("/", ","); //replace slashes with commas
            String[] ingredients = raw.split(","); //split by commas

            Set<Long> addedCanonicals = new HashSet<>();

            for (String ing : ingredients) { //look at each ingredient individually
                String normalized = ing.trim().toLowerCase(Locale.ROOT); //normalize by trimming whitespace and converting to lowercase

                Ingredient ingredient = getCanonicalIngredient(normalized);

                int canonicalId = (ingredient.getCanonicalId() != null) ? ingredient.getCanonicalId() : ingredient.getId();
                if (!addedCanonicals.contains(canonicalId)) {
                    ProductIngredient pi = new ProductIngredient();
                    pi.setProduct(product);

                    Ingredient canonicalIngredient = ingredientRepository.findById(canonicalId)
                        .orElse(ingredient); // fallback to the ingredient itself if canonical not found

                    pi.setIngredient(canonicalIngredient);
                    productIngredientRepository.save(pi);
                }
            }

            //product.setRawIngredients(null);
            productRepository.save(product);
        }
    }
}
