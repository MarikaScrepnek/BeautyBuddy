package com.beautybuddy.breakout;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.beautybuddy.breakout.dto.AddToBreakoutListDTO;
import com.beautybuddy.breakout.dto.DisplayBreakoutListProductDTO;
import com.beautybuddy.breakout.entity.BreakoutList;
import com.beautybuddy.breakout.entity.BreakoutListIngredient;
import com.beautybuddy.breakout.entity.BreakoutListProduct;
import com.beautybuddy.breakout.repo.BreakoutListIngredientRepository;
import com.beautybuddy.breakout.repo.BreakoutListProductRepository;
import com.beautybuddy.common.DTOMapper;
import com.beautybuddy.ingredient.dto.IngredientDTO;
import com.beautybuddy.ingredient.entity.Ingredient;
import com.beautybuddy.ingredient.repo.IngredientRepository;
import com.beautybuddy.product.entity.Product;
import com.beautybuddy.product.repo.ProductRepository;
import com.beautybuddy.user.UserRepository;
import com.beautybuddy.user.entity.User;

@Service
public class BreakoutListService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;

    private final BreakoutListProductRepository breakoutListProductRepository;
    private final BreakoutListIngredientRepository breakoutListIngredientRepository;

    public BreakoutListService(UserRepository userRepository, ProductRepository productRepository, IngredientRepository ingredientRepository, BreakoutListProductRepository breakoutListProductRepository, BreakoutListIngredientRepository breakoutListIngredientRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.ingredientRepository = ingredientRepository;
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

    public Set<DisplayBreakoutListProductDTO> getBreakoutListProducts (String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        BreakoutList breakoutList = user.getBreakoutList();
        Set<DisplayBreakoutListProductDTO> products = breakoutList.getProducts().stream()
            .map(DTOMapper::toDisplayBreakoutListProductDTO)
            .collect(Collectors.toSet());
        return products;
    }

    public Set<IngredientDTO> getBreakoutListIngredients(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        BreakoutList breakoutList = user.getBreakoutList();
        Set<IngredientDTO> ingredients = breakoutList.getIngredients().stream()
            .map(BreakoutListIngredient::getIngredient)
            .map(DTOMapper::toIngredientDTO)
            .collect(Collectors.toSet());
        return ingredients;
    }

    public void removeFromBreakoutList(String userEmail, AddToBreakoutListDTO removeFromBreakoutListDTO) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (removeFromBreakoutListDTO.productId() != null) {
            BreakoutListProduct breakoutListProduct = breakoutListProductRepository.findByBreakoutListIdAndProductId(user.getBreakoutList().getId(), removeFromBreakoutListDTO.productId())
                .orElseThrow(() -> new RuntimeException("Product not found in breakout list"));
            breakoutListProductRepository.delete(breakoutListProduct);
        }
        else if (removeFromBreakoutListDTO.ingredientId() != null) {
            BreakoutListIngredient breakoutListIngredient = breakoutListIngredientRepository.findByBreakoutListIdAndIngredientId(user.getBreakoutList().getId(), removeFromBreakoutListDTO.ingredientId())
                .orElseThrow(() -> new RuntimeException("Ingredient not found in breakout list"));
            breakoutListIngredientRepository.delete(breakoutListIngredient);
        }
        else {
            throw new RuntimeException("Either productId or ingredientId must be provided");
        }
    }
}