package com.beautybuddy.dto;

public class ProductIngredientDTO {
    private int id;
    private String name;
    private Integer canonicalId;

    public ProductIngredientDTO(int id, String name, Integer canonicalId) {
        this.id = id;
        this.name = name;
        this.canonicalId = canonicalId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCanonicalId() {
        return canonicalId;
    }
}
