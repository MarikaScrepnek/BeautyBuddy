package com.beautybuddy.routine.dto;

import java.math.BigDecimal;

public record DisplayRoutineItemDTO(
    Long id,
    Long productId,
    String productName,
    String productBrand,
    String productShadeName, //can be null
    String category,
    String productImageUrl,
    String productNotes, //can be null
    BigDecimal rating, //can be null
    //add occurence
    Integer order
) {}