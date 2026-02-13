package com.beautybuddy.review.dto;

import java.math.BigDecimal;
import java.util.List;

public record SubmitReviewDTO(
    Long productId,
    String shadeName,
    BigDecimal rating,
    String reviewText,
    List<String> imageLinks
)
{ 
}
