package com.beautybuddy.products;

import java.math.BigDecimal;
import java.util.List;

import com.beautybuddy.dto.BrandDTO;
import com.beautybuddy.dto.CategoryDTO;
import com.beautybuddy.ingredients.IngredientDTO;

public record ProductDTO (
    int id,
    String name,
    BrandDTO brand,
    CategoryDTO category,
    String image_link,
    String product_link,
    BigDecimal price,
    BigDecimal rating,
    List<IngredientDTO> ingredients,
    List<IngredientDTO> mayContainIngredients,
    List<ProductShadeDTO> shades
) {
    
}
