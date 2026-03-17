package com.beautybuddy.breakout;

import org.springframework.stereotype.Service;

import com.beautybuddy.breakout.dto.AddToBreakoutListDTO;
import com.beautybuddy.breakout.entity.BreakoutList;
import com.beautybuddy.breakout.entity.BreakoutListIngredient;
import com.beautybuddy.breakout.entity.BreakoutListProduct;
import com.beautybuddy.breakout.repo.BreakoutListIngredientRepository;
import com.beautybuddy.breakout.repo.BreakoutListProductRepository;
import com.beautybuddy.breakout.repo.BreakoutListRepository;
import com.beautybuddy.ingredient.entity.Ingredient;
import com.beautybuddy.ingredient.repo.IngredientRepository;
import com.beautybuddy.product.Product;
import com.beautybuddy.product.ProductRepository;
import com.beautybuddy.user.UserRepository;
import com.beautybuddy.user.entity.User;

@Service
public class BreakoutListService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;

    private final BreakoutListRepository breakoutListRepository;
    private final BreakoutListProductRepository breakoutListProductRepository;
    private final BreakoutListIngredientRepository breakoutListIngredientRepository;

    public BreakoutListService(UserRepository userRepository, ProductRepository productRepository, IngredientRepository ingredientRepository, BreakoutListRepository breakoutListRepository, BreakoutListProductRepository breakoutListProductRepository, BreakoutListIngredientRepository breakoutListIngredientRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.ingredientRepository = ingredientRepository;
        this.breakoutListRepository = breakoutListRepository;
        this.breakoutListProductRepository = breakoutListProductRepository;
        this.breakoutListIngredientRepository = breakoutListIngredientRepository;
    }

    public void addToBreakoutList(String userEmail, AddToBreakoutListDTO addToBreakoutListDTO) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (addToBreakoutListDTO.productId() != null) {
            Product product = productRepository.findById(addToBreakoutListDTO.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
            
            BreakoutListProduct breakoutListProduct = new BreakoutListProduct();
            breakoutListProduct.setBreakoutList(user.getBreakoutList());
            breakoutListProduct.setProduct(product);

            breakoutListProductRepository.save(breakoutListProduct);
        }
        else if (addToBreakoutListDTO.ingredientId() != null) {
            Ingredient ingredient = ingredientRepository.findById(addToBreakoutListDTO.ingredientId())
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
            
            BreakoutListIngredient breakoutListIngredient = new BreakoutListIngredient();
            breakoutListIngredient.setBreakoutList(user.getBreakoutList());
            breakoutListIngredient.setIngredient(ingredient);

            breakoutListIngredientRepository.save(breakoutListIngredient);
        }
        else {
            throw new RuntimeException("Either productId or ingredientId must be provided");
        }
    }
}