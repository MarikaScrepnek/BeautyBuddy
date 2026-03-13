package com.beautybuddy.wishlist.dto;

import java.math.BigDecimal;

public record WishlistItemDTO (
    Long id,
    Long productId,
    String productName,
    String baseCategoryName,
    String brandName,
    String shadeName,
    String imageLink,
    BigDecimal price,
    BigDecimal rating
) {}
