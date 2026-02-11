package com.beautybuddy.product;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.beautybuddy.common.entity.UpdatableEntity;
import com.beautybuddy.brand.Brand;
import com.beautybuddy.category.Category;
import com.beautybuddy.ingredient.MayContainIngredient;
import com.beautybuddy.ingredient.ProductIngredient;

@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "brand_id"})
    }
)
public class Product extends UpdatableEntity {

    @Column(nullable = false, columnDefinition = "CITEXT")
    private String name;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_link")
    private String imageLink;

    @Column(name = "product_link")
    private String productLink;

    @Column(precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "raw_ingredients", columnDefinition = "TEXT")
    private String rawIngredients;

    @Column(name = "may_contain_raw_ingredients", columnDefinition = "TEXT")
    private String rawMayContainIngredients;

    @Column(name = "review_count", nullable = false)
    private int reviewCount = 0;

    @Column(name = "is_discontinued", nullable = false)
    private boolean isDiscontinued = false;

    @OneToMany(
        mappedBy = "product",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<ProductIngredient> productIngredients = new HashSet<>();

    @OneToMany(
    mappedBy = "product",
    cascade = CascadeType.ALL,
    orphanRemoval = true
    )
    private Set<MayContainIngredient> mayContainIngredients = new HashSet<>();

    @OneToMany(
    mappedBy = "product",
    cascade = CascadeType.ALL,
    orphanRemoval = true
    )
    private Set<ProductShade> productShades = new HashSet<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getImageLink() { return imageLink; }
    public void setImageLink(String imageLink) { this.imageLink = imageLink; }

    public String getProductLink() { return productLink; }
    public void setProductLink(String productLink) { this.productLink = productLink; }
    
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public String getRawIngredients() { return rawIngredients; }
    public void setRawIngredients(String rawIngredients) { this.rawIngredients = rawIngredients; }

    public String getRawMayContainIngredients() { return rawMayContainIngredients; }
    public void setRawMayContainIngredients(String rawMayContainIngredients) { this.rawMayContainIngredients = rawMayContainIngredients; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public boolean isDiscontinued() { return isDiscontinued; }
    public void setDiscontinued(boolean discontinued) { this.isDiscontinued = discontinued; }

    public Set<ProductIngredient> getProductIngredients() {
        return productIngredients;
    }

    public Set<MayContainIngredient> getMayContainIngredients() {
        return mayContainIngredients;
    }

    public Set<ProductShade> getProductShades() {
        return productShades;
    }
}

