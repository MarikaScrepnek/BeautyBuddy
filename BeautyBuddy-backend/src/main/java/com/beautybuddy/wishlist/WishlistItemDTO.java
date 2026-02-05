package com.beautybuddy.wishlist;

public record WishlistItemDTO (
    int id,
    int productId,
    String productName,
    String shadeName,
    String imageLink
) {}
