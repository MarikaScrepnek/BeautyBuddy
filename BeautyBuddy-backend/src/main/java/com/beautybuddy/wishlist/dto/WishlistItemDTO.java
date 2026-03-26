package com.beautybuddy.wishlist.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WishlistItemDTO (
    Long id,
    Long productId,
    String productName,
    String baseCategoryName,
    String brandName,
    String shadeName,
    String imageLink,
    BigDecimal price,
    BigDecimal rating,
    LocalDateTime dateAdded
) {}
