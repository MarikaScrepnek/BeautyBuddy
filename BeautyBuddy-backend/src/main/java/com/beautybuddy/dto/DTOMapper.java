package com.beautybuddy.dto;

import com.beautybuddy.model.*;

import java.util.Comparator;
import java.util.List;

public class DTOMapper {

    public static BrandDTO toBrandDTO(Brand brand) {
        return new BrandDTO(brand.getBrand_id(), brand.getName());
    }

    public static CategoryDTO toCategoryDTO(Category category) {
        return new CategoryDTO(category.getCategory_id(), category.getName());
    }

    public static IngredientDTO toIngredientDTO(Ingredient ingredient) {
        return new IngredientDTO(ingredient.getName());
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
        List<IngredientDTO> sortedIngredients = product.getProductIngredients().stream()
        .sorted(Comparator.comparingInt(ProductIngredient::getPosition))
        .map(pi -> new IngredientDTO(pi.getIngredient().getName()))
        .toList();

        List<IngredientDTO> mayContain = product.getMayContainIngredients().stream()
        .map(mci -> new IngredientDTO(mci.getIngredient().getName()))
        .sorted(Comparator.comparing(IngredientDTO::name))
        .toList();

        return new ProductDTO(
            product.getProduct_id(),
            product.getName(),
            DTOMapper.toBrandDTO(product.getBrand()),
            DTOMapper.toCategoryDTO(product.getCategory()),
            product.getImage_link(),
            product.getProduct_link(),
            product.getPrice(),
            product.getRating(),
            sortedIngredients,
            mayContain
        );
    }
}
