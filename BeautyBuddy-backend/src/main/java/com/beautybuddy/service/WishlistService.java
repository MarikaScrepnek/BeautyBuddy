package com.beautybuddy.service;

import com.beautybuddy.dto.AddToWishlistRequestDTO;
import com.beautybuddy.dto.WishlistItemDTO;
import com.beautybuddy.repository.UserRepository;

import com.beautybuddy.repository.ProductRepository;
import com.beautybuddy.model.User;
import com.beautybuddy.model.WishlistItem;
import com.beautybuddy.model.Product;
import com.beautybuddy.model.ProductShade;
import com.beautybuddy.repository.ShadeRepository;
import com.beautybuddy.model.Wishlist;
import com.beautybuddy.repository.WishlistItemRepository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishlistService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ShadeRepository shadeRepository;
    private final WishlistItemRepository wishlistItemRepository;

    public WishlistService(UserRepository userRepository, ProductRepository productRepository, ShadeRepository shadeRepository, WishlistItemRepository wishlistItemRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.shadeRepository = shadeRepository;
        this.wishlistItemRepository = wishlistItemRepository;
    }

    @Transactional
    public void addToWishlist(String email, AddToWishlistRequestDTO request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductShade shade = shadeRepository.findByProductAndShadeName(product, request.shadeName())
            .orElseThrow(() -> new RuntimeException("Shade not found"));

        Wishlist wishlist = user.getWishlist();

        WishlistItem item = new WishlistItem();
        item.setWishlist(wishlist);
        item.setProduct(product);
        if (shade != null) {
            item.setShade(shade);
        }

        wishlistItemRepository.save(item);
    }

    public List<WishlistItemDTO> getWishlistItems(String username) {
        List<WishlistItem> items = wishlistItemRepository.findByWishlist_User_Email(username);
        List<WishlistItemDTO> result = new ArrayList<>();

        for (WishlistItem item : items) {
            Product product = item.getProduct();
            ProductShade shade = item.getShade();
            String shadeName = shade != null ? shade.getShadeName() : null;
            String imageLink = shade != null && shade.getImageLink() != null
                ? shade.getImageLink()
                : product.getImage_link();

            result.add(new WishlistItemDTO(
                item.getWishlist_item_id(),
                product.getProduct_id(),
                product.getName(),
                shadeName,
                imageLink
            ));
        }

        return result;
    }
}
