package com.beautybuddy.community.activity.entity;

import com.beautybuddy.community.activity.BaseActivityType;
import com.beautybuddy.wishlist.WishlistItem;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "wishlist_item_activity")
public class WishlistItemActivity extends BaseActivityType{
    @ManyToOne
    @JoinColumn(name = "wishlist_item_id", nullable = false)
    private WishlistItem wishlistItem;

    public WishlistItem getWishlistItem() {
        return wishlistItem;
    }
    public void setWishlistItem(WishlistItem wishlistItem) {
        this.wishlistItem = wishlistItem;
    }
}
