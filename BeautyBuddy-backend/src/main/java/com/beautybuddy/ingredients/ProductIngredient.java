package com.beautybuddy.ingredients;

import com.beautybuddy.products.Product;

import jakarta.persistence.*;

@Entity
@Table (name = "product_ingredient",
    uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "ingredient_id"})
)
public class ProductIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_ingredient_id", nullable = false)
    private int productIngredientId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "position", nullable = false)
    private int position;

    public ProductIngredient() {}

    public int getProductIngredientId() { return productIngredientId; }
    public void setProductIngredientId(int productIngredientId) { this.productIngredientId = productIngredientId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}