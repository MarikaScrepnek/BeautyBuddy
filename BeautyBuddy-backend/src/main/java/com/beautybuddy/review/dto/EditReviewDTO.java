package com.beautybuddy.review.dto;

import java.math.BigDecimal;
import java.util.List;

public record EditReviewDTO(
    String shadeName,
    BigDecimal rating,
    String title,
    String text,
    List<String> imageLinks
) {}
