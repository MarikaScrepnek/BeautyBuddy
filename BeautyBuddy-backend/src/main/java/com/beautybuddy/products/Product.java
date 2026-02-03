package com.beautybuddy.products;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.beautybuddy.model.Brand;
import com.beautybuddy.model.Category;
import com.beautybuddy.model.MayContainIngredient;
import com.beautybuddy.model.ProductIngredient;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ensure DB auto-increments
    private int product_id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column
    private BigDecimal price;

    @Column
    private String image_link;

    @Column
    private String product_link;

    @Column
    private BigDecimal rating;

    @Column(name = "raw_ingredients", columnDefinition = "TEXT")
    private String rawIngredients;

    @Column(name = "may_contain_raw_ingredients", columnDefinition = "TEXT")
    private String rawMayContainIngredients;
    
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

    public Product() {}

    public int getProduct_id() { return product_id; }
    public void setProduct_id(int product_id) { this.product_id = product_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getImage_link() { return image_link; }
    public void setImage_link(String image_link) { this.image_link = image_link; }

    public String getProduct_link() { return product_link; }
    public void setProduct_link(String product_link) { this.product_link = product_link; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public String getRawIngredients() { return rawIngredients; }
    public void setRawIngredients(String rawIngredients) { this.rawIngredients = rawIngredients; }

    public String getRawMayContainIngredients() { return rawMayContainIngredients; }
    public void setRawMayContainIngredients(String rawMayContainIngredients) { this.rawMayContainIngredients = rawMayContainIngredients; }

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

