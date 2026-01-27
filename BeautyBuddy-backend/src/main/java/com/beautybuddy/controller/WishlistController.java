package com.beautybuddy.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.beautybuddy.dto.AddToWishlistRequestDTO;
import com.beautybuddy.dto.WishlistItemDTO;
import com.beautybuddy.service.WishlistService;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addToWishlist(@RequestBody AddToWishlistRequestDTO request) {
        wishlistService.addToWishlist(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<WishlistItemDTO>> getWishlist(Principal principal) {
        List<WishlistItemDTO> wishlistItems = wishlistService.getWishlistItems(principal.getName());
        return ResponseEntity.ok(wishlistItems);
    }
}
