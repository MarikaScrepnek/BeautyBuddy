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

    public static ProductShadeDTO toProductShadeDTO(ProductShade ps) {
        return new ProductShadeDTO(
                ps.getShadeName(),
                ps.getImageLink(),
                ps.getProductLink()
        );
    }

    public static ProductDTO toProductDTO(Product product) {
        List<IngredientDTO> ingredients = product.getProductIngredients().stream()
                .sorted(Comparator.comparingInt(ProductIngredient::getPosition))
                .map(pi -> toIngredientDTO(pi.getIngredient()))
                .toList();

        List<IngredientDTO> mayContain = product.getMayContainIngredients().stream()
                .map(mci -> toIngredientDTO(mci.getIngredient()))
                .sorted(Comparator.comparing(IngredientDTO::name))
                .toList();

        List<ProductShadeDTO> shades = product.getProductShades().stream()
                .map(DTOMapper::toProductShadeDTO)
                .toList();

        return new ProductDTO(
                product.getProduct_id(),
                product.getName(),
                toBrandDTO(product.getBrand()),
                toCategoryDTO(product.getCategory()),
                product.getImage_link(),
                product.getProduct_link(),
                product.getPrice(),
                product.getRating(),
                ingredients,
                mayContain,
                shades
        );
    }
}