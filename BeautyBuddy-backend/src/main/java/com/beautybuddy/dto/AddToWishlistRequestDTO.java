package com.beautybuddy.dto;

public class AddToWishlistRequestDTO {
    private int product_id;
    private String shade_name;

    public AddToWishlistRequestDTO() {}

    public int getProduct_id() {
        return product_id;
    }
    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getShade_name() {
        return shade_name;
    }
    public void setShade_name(String shade_name) {
        this.shade_name = shade_name;
    }
}
