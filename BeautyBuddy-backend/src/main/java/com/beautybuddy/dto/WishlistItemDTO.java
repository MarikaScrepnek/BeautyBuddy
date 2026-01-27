package com.beautybuddy.dto;

public record WishlistItemDTO (
    int id,
    int productId,
    String productName,
    String shadeName,
    String imageLink
) {}
