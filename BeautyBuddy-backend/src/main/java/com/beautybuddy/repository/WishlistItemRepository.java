package com.beautybuddy.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.wishlists.WishlistItem;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Integer> {
	List<WishlistItem> findByWishlist_User_Email(String email);
}
