package com.beautybuddy.product;

import com.beautybuddy.common.entity.UpdatableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "product_shade",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"product_id", "shade_name"}), @UniqueConstraint(columnNames = {"product_shade_id", "product_id"})}
)
public class ProductShade extends UpdatableEntity{

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column (name = "shade_name", nullable = false, columnDefinition = "CITEXT")
    private String shadeName;

    @Column (name = "shade_hex_code")
    private String shadeHexCode;

    @Column (name = "shade_number")
    private int shadeNumber;

    @Column (name = "image_link")
    private String imageLink;

    @Column (name = "product_link")
    private String productLink;

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public String getShadeName() {
        return shadeName;
    }
    public void setShadeName(String shadeName) {
        this.shadeName = shadeName;
    }
    public String getShadeHexCode() {
        return shadeHexCode;
    }
    public void setShadeHexCode(String shadeHexCode) {
        this.shadeHexCode = shadeHexCode;
    }

    public int getShadeNumber() {
        return shadeNumber;
    }

    public void setShadeNumber(int shadeNumber) {
        this.shadeNumber = shadeNumber;
    }

    public String getImageLink() {
        return imageLink;
    }
    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
    public String getProductLink() {
        return productLink;
    }
    public void setProductLink(String productLink) {
        this.productLink = productLink;
    }
}
