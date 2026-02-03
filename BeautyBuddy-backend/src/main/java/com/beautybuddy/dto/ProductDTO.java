package com.beautybuddy.dto;

import java.math.BigDecimal;
import java.util.List;

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
