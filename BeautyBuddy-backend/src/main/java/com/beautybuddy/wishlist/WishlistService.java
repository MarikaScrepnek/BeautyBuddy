package com.beautybuddy.wishlist;

import com.beautybuddy.product.Product;
import com.beautybuddy.product.ProductRepository;
import com.beautybuddy.product.ProductShade;
import com.beautybuddy.product.ProductShadeRepository;
import com.beautybuddy.user.UserRepository;
import com.beautybuddy.user.entity.User;
import com.beautybuddy.wishlist.dto.AddToWishlistRequestDTO;
import com.beautybuddy.wishlist.dto.WishlistItemDTO;
import com.beautybuddy.wishlist.entity.Wishlist;
import com.beautybuddy.wishlist.entity.WishlistItem;

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

        ProductShade shade = null;
        if (request.shadeName() != null) {
            shade = shadeRepository.findByProductAndShadeName(product, request.shadeName())
                .orElseThrow(() -> new RuntimeException("Shade not found"));
        }
        if (!product.getProductShades().isEmpty() && shade == null) {
            shade = shadeRepository.findByProductAndShadeNumber(product, 1)
                .orElseThrow(() -> new RuntimeException("Default shade not found"));
        }

        Wishlist wishlist = user.getWishlist();

        WishlistItem item = new WishlistItem();
        item.setWishlist(wishlist);
        item.setProduct(product);
        item.setShade(shade);

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
                product.getCategory().getBaseCategory().getName(),
                product.getBrand().getName(),
                shadeName,
                imageLink,
                product.getPrice(),
                product.getRating()
            ));
        }

        return result;
    }

    public List<WishlistItemDTO> searchWishlistItems(String username, String query) {
        List<WishlistItem> items = wishlistItemRepository.findByWishlist_User_Email(username);
        List<WishlistItemDTO> result = new ArrayList<>();

        for (WishlistItem item : items) {
            Product product = item.getProduct();
            if (product.getName().toLowerCase().contains(query.toLowerCase()) ||
                product.getBrand().getName().toLowerCase().contains(query.toLowerCase()) ||
                item.getShade() != null && item.getShade().getShadeName().toLowerCase().contains(query.toLowerCase())) {
                
                ProductShade shade = item.getShade();
                String shadeName = shade != null ? shade.getShadeName() : null;
                String imageLink = shade != null && shade.getImageLink() != null
                    ? shade.getImageLink()
                    : product.getImageLink();

                result.add(new WishlistItemDTO(
                    item.getId(),
                    product.getId(),
                    product.getName(),
                    product.getCategory().getBaseCategory().getName(),
                    product.getBrand().getName(),
                    shadeName,
                    imageLink,
                    product.getPrice(),
                    product.getRating()
                ));
            }
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