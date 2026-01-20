package com.beautybuddy.model;

import jakarta.persistence.*;

@Entity
@Table (name = "product_ingredient",
    uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "ingredient_id"})
)
public class ProductIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int product_ingredient_id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "position", nullable = false)
    private int position;

    public ProductIngredient() {}

    public int getProduct_ingredient_id() { return product_ingredient_id; }
    public void setProduct_ingredient_id(int product_ingredient_id) { this.product_ingredient_id = product_ingredient_id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}