package com.beautybuddy.report.entity;

import com.beautybuddy.product.entity.Product;
import com.beautybuddy.report.BaseReport;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "product_report",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "product_id"})
)
public class ProductReport extends BaseReport {
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
}
