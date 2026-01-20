package com.beautybuddy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.util.HashSet;
import java.util.Set;

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
    private Float price;

    @Column
    private String image_link;

    @Column
    private String product_link;

    @Column
    private Float rating;

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


    public Product() {}

    public int getProduct_id() { return product_id; }
    public void setProduct_id(int product_id) { this.product_id = product_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }

    public String getImage_link() { return image_link; }
    public void setImage_link(String image_link) { this.image_link = image_link; }

    public String getProduct_link() { return product_link; }
    public void setProduct_link(String product_link) { this.product_link = product_link; }

    public Float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

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
}

