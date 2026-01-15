package com.beautybuddy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ensure DB auto-increments
    private int product_id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private Float price;
    private String image_link;
    private String product_link;
    private String description;
    private Float rating;

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

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
}

