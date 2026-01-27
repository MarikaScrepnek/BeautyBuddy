package com.beautybuddy.repository;

import com.beautybuddy.model.WishlistItem;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Integer> {
}
