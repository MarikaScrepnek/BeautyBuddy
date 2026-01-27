package com.beautybuddy.service;

import com.beautybuddy.dto.AddToWishlistRequestDTO;
import com.beautybuddy.dto.WishlistItemDTO;

import java.util.ArrayList;
import java.util.List;

public class WishlistService {
    public void addToWishlist(String username, AddToWishlistRequestDTO request) {
        // Implementation for adding item to wishlist
    }

    public List<WishlistItemDTO> getWishlistItems(String username) {
        // Implementation for retrieving wishlist items
        return new ArrayList<>();
    }
}
