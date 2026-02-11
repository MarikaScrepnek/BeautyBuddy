package com.beautybuddy.wishlist;

public record WishlistItemDTO (
    Long id,
    Long productId,
    String productName,
    String shadeName,
    String imageLink
) {}
