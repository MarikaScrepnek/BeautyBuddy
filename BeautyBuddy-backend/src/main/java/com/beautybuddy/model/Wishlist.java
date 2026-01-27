package com.beautybuddy.model;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;

import java.util.Set;


@Table(name = "wishlist",
    uniqueConstraints = @jakarta.persistence.UniqueConstraint(columnNames = {"user_id"})
)
@Entity
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int wishlist_id;

    @Column(name = "user_id", nullable = false)
    private int user_id;

    @OneToMany(
        mappedBy = "wishlist",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<WishlistItem> wishlist_items;

    @Column(name = "created_at", nullable = false)
    private String created_at;

    public Wishlist(int wishlist_id, int user_id, Set<WishlistItem> wishlist_items, String created_at) {
        this.wishlist_id = wishlist_id;
        this.user_id = user_id;
        this.wishlist_items = wishlist_items;
        this.created_at = created_at;
    }

    public int getWishlistId() {
        return wishlist_id;
    }

    public void setWishlistId(int wishlist_id) {
        this.wishlist_id = wishlist_id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public Set<WishlistItem> getItems() {
        return wishlist_items;
    }

    public void setItems(Set<WishlistItem> wishlist_items) {
        this.wishlist_items = wishlist_items;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String createdAt) {
        this.created_at = createdAt;
    }

}