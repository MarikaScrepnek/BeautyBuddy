package com.beautybuddy.wishlist;

public record WishlistItemDTO (
    Long id,
    int productId,
    String productName,
    String shadeName,
    String imageLink
) {}
