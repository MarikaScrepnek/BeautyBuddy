package com.beautybuddy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "wishlist_item",
         uniqueConstraints = @UniqueConstraint(columnNames = {"wishlist_id", "product_id"})
)
public class WishlistItem {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int wishlist_item_id;

    @Column(name = "wishlist_id", nullable = false)
    private Wishlist wishlist_id;

    @Column(name = "product_id", nullable = false)
    private Product product_id;

    public int getWishlist_item_id() {
        return wishlist_item_id;
    }

    public void setWishlist_item_id(int wishlist_item_id) {
        this.wishlist_item_id = wishlist_item_id;
    }

    public Wishlist getWishlist_id() {
        return wishlist_id;
    }

    public void setWishlist_id(Wishlist wishlist_id) {
        this.wishlist_id = wishlist_id;
    }

    public Product getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Product product_id) {
        this.product_id = product_id;
    }
}