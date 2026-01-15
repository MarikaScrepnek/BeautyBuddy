package com.beautybuddy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int category_id;

    private String name;
    private Integer parent_category_id;

    public Category() {}

    public int getCategory_id() { return category_id; }
    public void setCategory_id(int category_id) { this.category_id = category_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getParent_category_id() { return parent_category_id; }
    public void setParent_category_id(Integer parent_category_id) { this.parent_category_id = parent_category_id; }
}

