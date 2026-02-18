package com.beautybuddy.qa.dto;

public record DisplayQuestion(
    Long id,
    Long productId,
    String text,
    String authorName
) {
    
}
