package com.beautybuddy.ingredient;

import com.beautybuddy.product.Product;

import jakarta.persistence.*;

@Entity
@Table (name = "may_contain_ingredient",
    uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "ingredient_id"})
)
public class MayContainIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "may_contain_ingredient_id")
    private int mayContainIngredientId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    public MayContainIngredient() {}

    public int getMayContainIngredientId() {
        return mayContainIngredientId;
    }
    public void setMayContainIngredientId(int mayContainIngredientId) {
        this.mayContainIngredientId = mayContainIngredientId;
    }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
}