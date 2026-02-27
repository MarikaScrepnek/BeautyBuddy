package com.beautybuddy.wishlist;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.wishlist.entity.WishlistItem;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Integer> {
	List<WishlistItem> findByWishlist_User_Email(String email);
}
