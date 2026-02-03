package com.beautybuddy.products;

import jakarta.persistence.*;

@Entity
@Table(name = "product_shade",
    uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "shade_name"})
)
public class ProductShade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int product_shade_id;

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
    public int getProduct_shade_id() {
        return product_shade_id;
    }
    public void setProduct_shade_id(int product_shade_id) {
        this.product_shade_id = product_shade_id;
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
