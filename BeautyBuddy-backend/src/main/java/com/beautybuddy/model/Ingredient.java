package com.beautybuddy.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

@Entity
@Table (name = "ingredient",
    UniqueConstraints = @UniqueConstraint(columnNames = "normalized_name")
)
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ingredient_id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "normalized_name", unique = true, nullable = false)
    private String normalizedName;

    public Ingredient() {}

    public int getIngredient_id() { return ingredient_id; }
    public void setIngredient_id(int ingredient_id) { this.ingredient_id = ingredient_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNormalizedName() { return normalizedName; }
    public void setNormalizedName(String normalizedName) { this.normalizedName = normalizedName; }
}