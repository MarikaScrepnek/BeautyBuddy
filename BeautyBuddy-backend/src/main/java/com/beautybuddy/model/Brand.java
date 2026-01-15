package com.beautybuddy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int brand_id;

    @Column(unique = true, nullable = false)
    private String name;

    public Brand() {}

    public int getBrand_id() { return brand_id; }
    public void setBrand_id(int brand_id) { this.brand_id = brand_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

