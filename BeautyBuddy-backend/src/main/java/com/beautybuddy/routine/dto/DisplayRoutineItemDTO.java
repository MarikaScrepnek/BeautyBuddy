package com.beautybuddy.routine.dto;

import java.math.BigDecimal;
import java.util.List;

import com.beautybuddy.product.dto.ProductShadeDTO;

public record DisplayRoutineItemDTO(
    Long id,
    Long productId,
    String productName,
    String productBrand,
    String productShadeName, //can be null
    List<ProductShadeDTO> allShades, //can be null
    String category,
    String productImageUrl,
    String productNotes, //can be null
    BigDecimal rating, //can be null
    Long reviewId, //can be null
    //add occurence
    Integer order
) {}