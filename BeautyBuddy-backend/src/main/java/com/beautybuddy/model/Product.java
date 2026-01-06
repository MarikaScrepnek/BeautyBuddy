package com.beautybuddy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ensure DB auto-increments
    private int product_id;

    private String name;
    private int brand_id;
    private int category_id;
    private Float price;
    private String image_link;
    private String product_link;
    private String description;
    private Float rating;

    // Empty constructor required by JPA
    public Product() {}

    // Getters and setters
    public int getProduct_id() { return product_id; }
    public void setProduct_id(int product_id) { this.product_id = product_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getBrand_id() { return brand_id; }
    public void setBrand_id(int brand_id) { this.brand_id = brand_id; }

    public int getCategory_id() { return category_id; }
    public void setCategory_id(int category_id) { this.category_id = category_id; }

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

