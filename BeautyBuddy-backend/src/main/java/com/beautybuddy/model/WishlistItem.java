package com.beautybuddy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public int getWishlist_item_id() {
        return wishlist_item_id;
    }

    public void setWishlist_item_id(int wishlist_item_id) {
        this.wishlist_item_id = wishlist_item_id;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}