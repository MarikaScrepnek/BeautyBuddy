package com.beautybuddy.wishlist.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

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
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime dateAdded
) {}
