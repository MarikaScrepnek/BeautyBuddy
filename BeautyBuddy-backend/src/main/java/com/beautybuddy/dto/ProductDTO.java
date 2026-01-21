package com.beautybuddy.dto;

import java.util.List;

public record ProductDTO (
    int id,
    String name,
    BrandDTO brand,
    CategoryDTO category,
    String image_link,
    String product_link,
    Float price,
    Float rating,
    List<IngredientDTO> ingredients,
    List<IngredientDTO> mayContainIngredients,
    List<ProductShadeDTO> shades
) {
    
}
