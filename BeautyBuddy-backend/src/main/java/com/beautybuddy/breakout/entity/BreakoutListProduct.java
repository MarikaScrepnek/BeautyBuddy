package com.beautybuddy.breakout.entity;

import com.beautybuddy.common.entity.BaseEntity;
import com.beautybuddy.product.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "breakout_list_product",
    uniqueConstraints = @UniqueConstraint(columnNames = {"breakout_list_id", "product_id"})
)
public class BreakoutListProduct extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "breakout_list_id", nullable = false)
    private BreakoutList breakoutList;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public BreakoutList getBreakoutList() {
        return breakoutList;
    }
    public void setBreakoutList(BreakoutList breakoutList) {
        this.breakoutList = breakoutList;
    }

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    
}
