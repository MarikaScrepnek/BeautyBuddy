package com.beautybuddy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.beautybuddy.dto.AddToWishlistRequestDTO;
import com.beautybuddy.dto.WishlistItemDTO;
import com.beautybuddy.security.CustomUserDetails;
import com.beautybuddy.wishlists.WishlistService;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addToWishlist(@RequestBody AddToWishlistRequestDTO request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        wishlistService.addToWishlist(userDetails.getEmail(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove")
    public ResponseEntity<Void> removeFromWishlist(@RequestBody AddToWishlistRequestDTO request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        wishlistService.removeFromWishlist(userDetails.getEmail(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<WishlistItemDTO>> getWishlist(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<WishlistItemDTO> wishlistItems = wishlistService.getWishlistItems(userDetails.getEmail());
        return ResponseEntity.ok(wishlistItems);
    }
}
