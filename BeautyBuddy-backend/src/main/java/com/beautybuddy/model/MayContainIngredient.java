package com.beautybuddy.model;

import jakarta.persistence.*;

@Entity
@Table (name = "may_contain_ingredient",
    uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "ingredient_id"})
)
public class MayContainIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int may_contain_ingredient_id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    public MayContainIngredient() {}

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
}