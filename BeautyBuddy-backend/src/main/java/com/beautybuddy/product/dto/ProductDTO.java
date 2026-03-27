package com.beautybuddy.product.dto;

import java.math.BigDecimal;
import java.util.List;

import com.beautybuddy.brand.BrandDTO;
import com.beautybuddy.category.CategoryDTO;
import com.beautybuddy.ingredient.dto.IngredientDTO;

public record ProductDTO (
    Long id,
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
