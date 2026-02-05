package com.beautybuddy.product;

import jakarta.persistence.*;

@Entity
@Table(name = "product_shade",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"product_id", "shade_name"}), @UniqueConstraint(columnNames = {"product_shade_id", "product_id"})}
)
public class ProductShade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_shade_id")
    private int productShadeId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column (name = "shade_name", nullable = false)
    private String shadeName;

    @Column (name = "shade_hex_code")
    private String shadeHexCode;

    @Column (name = "shade_number")
    private int shadeNumber;

    @Column (name = "image_link")
    private String imageLink;

    @Column (name = "product_link")
    private String productLink;

    // Getters and Setters
    public int getProductShadeId() {
        return productShadeId;
    }
    public void setProductShadeId(int productShadeId) {
        this.productShadeId = productShadeId;
    }
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
