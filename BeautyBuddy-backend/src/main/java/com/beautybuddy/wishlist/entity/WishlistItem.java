package com.beautybuddy.wishlist.entity;

import com.beautybuddy.common.entity.BaseEntity;
import com.beautybuddy.product.entity.Product;
import com.beautybuddy.product.entity.ProductShade;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "wishlist_item",
         uniqueConstraints = @UniqueConstraint(columnNames = {"wishlist_id", "product_id"})
)
public class WishlistItem extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "shade_id")
    private ProductShade shade;

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

    public ProductShade getShade() {
        return shade;
    }
    public void setShade(ProductShade shade) {
        this.shade = shade;
    }
}