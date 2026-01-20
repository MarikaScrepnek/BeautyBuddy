package com.beautybuddy.dto;

public class MayContainIngredientDTO {
    private int id;
    private String name;
    private Integer canonicalId;

    public MayContainIngredientDTO(int id, String name, Integer canonicalId) {
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
