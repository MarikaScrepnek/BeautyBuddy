package com.beautybuddy.service;

import com.beautybuddy.dto.AddToWishlistRequestDTO;
import com.beautybuddy.dto.WishlistItemDTO;
import com.beautybuddy.repository.UserRepository;
import com.beautybuddy.repository.ProductRepository;
import com.beautybuddy.model.User;
import com.beautybuddy.model.WishlistItem;
import com.beautybuddy.model.Product;

import java.util.ArrayList;
import java.util.List;

public class WishlistService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ShadeRepository shadeRepository;
    private final WishlistItemRepository wishlistItemRepository;

    public void addToWishlist(String username, AddToWishlistRequestDTO request) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductShade shade = shadeRepository.findByProductAndName(product, request.shadeName())
            .orElseThrow(() -> new RuntimeException("Shade not found"));

        Wishlist wishlist = user.getWishlist();

        WishlistItem item = new WishlistItem();
        item.setWishlist(wishlist);
        item.setProduct(product);
        if (shade != null) {
            item.setShade(shade);
        }

        wishlist.getItems().add(item);
        wishlistItemRepository.save(item);
    }

    public List<WishlistItemDTO> getWishlistItems(String username) {
        // Implementation for retrieving wishlist items
        return new ArrayList<>();
    }
}
