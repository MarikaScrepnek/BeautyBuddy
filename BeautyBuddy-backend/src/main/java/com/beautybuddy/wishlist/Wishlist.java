package com.beautybuddy.wishlist;

import com.beautybuddy.common.entity.ForeignKeyIdEntity;
import com.beautybuddy.user.User;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CascadeType;
import java.time.LocalDateTime;
import java.util.Set;

@Table(name = "wishlist",
    uniqueConstraints = @jakarta.persistence.UniqueConstraint(columnNames = {"account_id"})
)
@Entity
public class Wishlist extends ForeignKeyIdEntity {
    @Id
    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private User user;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updated_at;

    @OneToMany(
        mappedBy = "wishlist",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<WishlistItem> wishlist_items;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getUpdatedAt() {
        return updated_at;
    }
    public void setUpdatedAt(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public Set<WishlistItem> getItems() {
        return wishlist_items;
    }

    public void setItems(Set<WishlistItem> wishlist_items) {
        this.wishlist_items = wishlist_items;
    }

    @Override
    protected Long getForeignKeyId() {
        return user.getId();
    }
}