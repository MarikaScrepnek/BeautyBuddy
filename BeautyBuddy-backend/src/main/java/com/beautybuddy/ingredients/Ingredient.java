package com.beautybuddy.ingredients;

import jakarta.persistence.*;

@Entity
@Table (name = "ingredient",
    uniqueConstraints = @UniqueConstraint(columnNames = "normalized_name")
)
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id", nullable = false)
    private int ingredientId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "canonical_id")
    private Integer canonicalId;

    public Ingredient() {}

    public int getIngredientId() { return ingredientId; }
    public void setIngredientId(int ingredientId) { this.ingredientId = ingredientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getCanonicalId() { return canonicalId; }
    public void setCanonicalId(Integer canonicalId) { this.canonicalId = canonicalId; }
}