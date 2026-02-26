package com.beautybuddy.wishlist;

import com.beautybuddy.product.Product;
import com.beautybuddy.product.ProductRepository;
import com.beautybuddy.product.ProductShade;
import com.beautybuddy.product.ProductShadeRepository;
import com.beautybuddy.user.UserRepository;
import com.beautybuddy.user.entity.User;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishlistService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductShadeRepository shadeRepository;
    private final WishlistItemRepository wishlistItemRepository;

    public WishlistService(UserRepository userRepository, ProductRepository productRepository, ProductShadeRepository shadeRepository, WishlistItemRepository wishlistItemRepository) {
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
                : product.getImageLink();

            result.add(new WishlistItemDTO(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getBrand().getName(),
                shadeName,
                imageLink,
                product.getPrice(),
                product.getRating()
            ));
        }

        return result;
    }

    @Transactional
    public void removeFromWishlist(String email, AddToWishlistRequestDTO request) {
        List<WishlistItem> items = wishlistItemRepository.findByWishlist_User_Email(email);
        WishlistItem target = null;
        for (WishlistItem item : items) {
            if (item.getProduct().getId() == request.productId()) {
                if (request.shadeName() == null && item.getShade() == null) {
                    target = item;
                    break;
                } else if (item.getShade() != null && item.getShade().getShadeName().equals(request.shadeName())) {
                    target = item;
                    break;
                }
            }
        }
        if (target != null) {
            wishlistItemRepository.delete(target);
        } else {
            throw new RuntimeException("Wishlist item not found");
        }
    }
}