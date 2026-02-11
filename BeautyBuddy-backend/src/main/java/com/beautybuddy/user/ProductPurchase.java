package com.beautybuddy.user;

import com.beautybuddy.common.entity.BaseEntity;
import com.beautybuddy.product.Product;
import com.beautybuddy.product.ProductShade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "product_purchase",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"account_id", "product_id", "shade_id"})
    }
)
public class ProductPurchase extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "shade_id")
    private ProductShade shade;

    @Column(name = "times_purchased", nullable = false)
    private int timesPurchased = 1;

    @Column(name = "last_purchased_at", nullable = false)
    private LocalDateTime lastPurchasedAt;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductShade getShade() {
        return shade;
    }
    public void setShade(ProductShade shade) {
        this.shade = shade;
    }

    public int getTimesPurchased() {
        return timesPurchased;
    }
    public void setTimesPurchased(int timesPurchased) {
        this.timesPurchased = timesPurchased;
    }

    public LocalDateTime getLastPurchasedAt() {
        return lastPurchasedAt;
    }
    public void setLastPurchasedAt(LocalDateTime lastPurchasedAt) {
        this.lastPurchasedAt = lastPurchasedAt;
    }
}
