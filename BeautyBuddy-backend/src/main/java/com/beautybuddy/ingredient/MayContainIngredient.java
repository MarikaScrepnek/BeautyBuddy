package com.beautybuddy.ingredient;

import com.beautybuddy.product.Product;
import com.beautybuddy.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table (name = "may_contain_ingredient",
    uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "ingredient_id"})
)
public class MayContainIngredient extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
}