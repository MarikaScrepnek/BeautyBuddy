package com.beautybuddy.dto;

import java.util.List;

public record ProductDTO (
    int id,
    String name,
    BrandDTO brand,
    CategoryDTO category,
    float price,
    String imageUrl,
    String productUrl,
    float rating,
    List<ProductIngredientDTO> ingredients,
    List<MayContainIngredientDTO> mayContainIngredients
) {
    
}
