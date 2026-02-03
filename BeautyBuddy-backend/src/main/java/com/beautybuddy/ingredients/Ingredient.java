package com.beautybuddy.ingredients;

import jakarta.persistence.*;

@Entity
@Table (name = "ingredient",
    uniqueConstraints = @UniqueConstraint(columnNames = "normalized_name")
)
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ingredient_id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "canonical_id")
    private Integer canonicalId;

    public Ingredient() {}

    public int getIngredient_id() { return ingredient_id; }
    public void setIngredient_id(int ingredient_id) { this.ingredient_id = ingredient_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getCanonicalId() { return canonicalId; }
    public void setCanonicalId(Integer canonicalId) { this.canonicalId = canonicalId; }
}