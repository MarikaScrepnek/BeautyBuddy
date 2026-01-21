package com.beautybuddy.dto;

import java.util.List;

public record ProductDTO (
    int id,
    String name,
    BrandDTO brand,
    CategoryDTO category,
    float price,
    String image_link,
    String product_link,
    float rating,
    List<ProductIngredientDTO> ingredients,
    List<MayContainIngredientDTO> mayContainIngredients
) {
    
}
