package com.beautybuddy.breakout.dto;

import java.util.List;

import com.beautybuddy.ingredient.dto.IngredientDTO;

public record DisplayBreakoutListProductDTO(
    Long id,
    Long productId,
    String productName,
    String productBrand,
    String productImageUrl,
    List<IngredientDTO> ingredients
) {}
