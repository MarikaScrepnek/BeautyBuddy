package com.beautybuddy.wishlist;

import java.math.BigDecimal;

public record WishlistItemDTO (
    Long id,
    Long productId,
    String productName,
    String brandName,
    String shadeName,
    String imageLink,
    BigDecimal price,
    BigDecimal rating
) {}
