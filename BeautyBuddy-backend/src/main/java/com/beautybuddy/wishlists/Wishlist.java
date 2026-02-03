package com.beautybuddy.wishlists;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;

import java.util.Set;

import com.beautybuddy.model.User;

import java.sql.Timestamp;


@Table(name = "wishlist",
    uniqueConstraints = @jakarta.persistence.UniqueConstraint(columnNames = {"user_id"})
)
@Entity
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int wishlist_id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(
        mappedBy = "wishlist",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<WishlistItem> wishlist_items;

    @Column(name = "created_at", nullable = false)
    private Timestamp created_at;

    public Wishlist() {}

    public int getWishlistId() {
        return wishlist_id;
    }

    public void setWishlistId(int wishlist_id) {
        this.wishlist_id = wishlist_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<WishlistItem> getItems() {
        return wishlist_items;
    }

    public void setItems(Set<WishlistItem> wishlist_items) {
        this.wishlist_items = wishlist_items;
    }

    public Timestamp getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.created_at = createdAt;
    }

}