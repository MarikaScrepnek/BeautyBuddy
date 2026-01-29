package com.beautybuddy.repository;

import com.beautybuddy.model.WishlistItem;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Integer> {
	List<WishlistItem> findByWishlist_User_Email(String email);
}
