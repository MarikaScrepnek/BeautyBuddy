package com.beautybuddy.wishlist;

import com.beautybuddy.product.entity.Product;
import com.beautybuddy.product.entity.ProductShade;
import com.beautybuddy.product.repo.ProductRepository;
import com.beautybuddy.product.repo.ProductShadeRepository;
import com.beautybuddy.user.UserRepository;
import com.beautybuddy.user.entity.User;
import com.beautybuddy.wishlist.dto.AddToWishlistRequestDTO;
import com.beautybuddy.wishlist.dto.WishlistItemDTO;
import com.beautybuddy.wishlist.entity.Wishlist;
import com.beautybuddy.wishlist.entity.WishlistItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
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
                product.getRating(),
                item.getCreatedAt()
            ));
        }

        return result;
    }

    public List<WishlistItemDTO> getWishlist(
        String username,
        String sort,          // e.g. "price_asc", "rating_desc", "added_desc"
        String query,         // optional search
        String category,      // optional category filter
        String priceRange     // optional price range filter
) {
    List<WishlistItemDTO> items = getWishlistItems(username);

    // 1) filtering
    List<WishlistItemDTO> filtered = new ArrayList<>();
    for (WishlistItemDTO dto : items) {
        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase();
            boolean matches =
                    dto.productName().toLowerCase().contains(q) ||
                    dto.brandName().toLowerCase().contains(q) ||
                    (dto.shadeName() != null && dto.shadeName().toLowerCase().contains(q));
            if (!matches) continue;
        }

        if (category != null && !category.isBlank()) {
            if (!dto.baseCategoryName().equalsIgnoreCase(category)) continue;
        }

        if (priceRange != null) {
            BigDecimal price = dto.price();
            switch (priceRange) {
                case "below_20":
                    if (price.compareTo(new BigDecimal("20")) >= 0) continue;
                    break;
                case "20_50":
                    if (price.compareTo(new BigDecimal("20")) < 0 ||
                        price.compareTo(new BigDecimal("50")) > 0) continue;
                    break;
                case "above_50":
                    if (price.compareTo(new BigDecimal("50")) <= 0) continue;
                    break;
                default:
                    break;
            }
        }

        filtered.add(dto);
    }

    // 2) sorting
    if (sort != null) {
        switch (sort) {
            case "price_asc" -> filtered.sort((a, b) -> a.price().compareTo(b.price()));
            case "price_desc" -> filtered.sort((a, b) -> b.price().compareTo(a.price()));
            case "rating_desc" -> filtered.sort((a, b) -> {
                BigDecimal ra = a.rating();
                BigDecimal rb = b.rating();
                if (ra == null && rb == null) return 0;
                if (ra == null) return 1;
                if (rb == null) return -1;
                return rb.compareTo(ra);
            });
            case "rating_asc" -> filtered.sort((a, b) -> {
                BigDecimal ra = a.rating();
                BigDecimal rb = b.rating();
                if (ra == null && rb == null) return 0;
                if (ra == null) return 1;
                if (rb == null) return -1;
                return ra.compareTo(rb);
            });
            case "added_asc" -> filtered.sort((a, b) -> a.dateAdded().compareTo(b.dateAdded()));
            case "added_desc" -> filtered.sort((a, b) -> b.dateAdded().compareTo(a.dateAdded()));
            default -> { }
        }
    }

    return filtered;
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
                    product.getRating(),
                    item.getCreatedAt()
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

    public List<WishlistItemDTO> sortWishlist (String email, String type) {
        List<WishlistItem> items = wishlistItemRepository.findByWishlist_User_Email(email);
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
                product.getRating(),
                item.getCreatedAt()
            ));
        }

        if (type.equals("price_asc")) {
            result.sort((a, b) -> a.price().compareTo(b.price()));
        } else if (type.equals("price_desc")) {
            result.sort((a, b) -> b.price().compareTo(a.price()));
        } else if (type.equals("rating_desc")) {
            result.sort(
                Comparator.comparing(
                    WishlistItemDTO::rating,
                    Comparator.nullsFirst(BigDecimal::compareTo)
                ).reversed()
            );
        } else if (type.equals("rating_asc")) {
            result.sort(
                Comparator.comparing(
                    WishlistItemDTO::rating,
                    Comparator.nullsLast(BigDecimal::compareTo)
                )
            );
        } else if (type.equals("added_asc")) {
            result.sort((a, b) -> a.dateAdded().compareTo(b.dateAdded()));
        } else if (type.equals("added_desc")) {
            result.sort((a, b) -> b.dateAdded().compareTo(a.dateAdded()));
        }

        return result;
    }
}