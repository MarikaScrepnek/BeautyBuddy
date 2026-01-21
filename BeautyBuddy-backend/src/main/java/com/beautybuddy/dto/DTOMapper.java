package com.beautybuddy.dto;

import com.beautybuddy.model.*;

public class DTOMapper {

    public static BrandDTO toBrandDTO(Brand brand) {
        return new BrandDTO(brand.getBrand_id(), brand.getName());
    }

    public static CategoryDTO toCategoryDTO(Category category) {
        return new CategoryDTO(category.getCategory_id(), category.getName());
    }

    public static IngredientDTO toIngredientDTO(Ingredient ingredient) {
        return new IngredientDTO(ingredient.getIngredient_id(), ingredient.getName());
    }

    public static ProductIngredientDTO toProductIngredientDTO(ProductIngredient pi) {
        return new ProductIngredientDTO(
                toIngredientDTO(pi.getIngredient()),
                pi.getPosition()
        );
    }

    public static MayContainIngredientDTO toMayContainIngredientDTO(MayContainIngredient mci) {
        return new MayContainIngredientDTO(
                toIngredientDTO(mci.getIngredient())
        );
    }

    public static ProductDTO toProductDTO(Product product) {
        return new ProductDTO(
                product.getProduct_id(),
                product.getName(),
                toBrandDTO(product.getBrand()),
                toCategoryDTO(product.getCategory()),
                product.getPrice(),
                product.getImage_link(),
                product.getProduct_link(),
                product.getRating(),
                product.getProductIngredients().stream()
                        .map(DTOMapper::toProductIngredientDTO)
                        .toList(),
                product.getMayContainIngredients().stream()
                        .map(DTOMapper::toMayContainIngredientDTO)
                        .toList()
        );
    }
}
