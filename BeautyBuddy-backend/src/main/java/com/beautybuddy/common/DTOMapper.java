package com.beautybuddy.common;

import com.beautybuddy.brand.Brand;
import com.beautybuddy.brand.BrandDTO;
import com.beautybuddy.category.Category;
import com.beautybuddy.category.CategoryDTO;
import com.beautybuddy.ingredient.Ingredient;
import com.beautybuddy.ingredient.IngredientDTO;
import com.beautybuddy.ingredient.MayContainIngredient;
import com.beautybuddy.ingredient.MayContainIngredientDTO;
import com.beautybuddy.ingredient.ProductIngredient;
import com.beautybuddy.ingredient.ProductIngredientDTO;
import com.beautybuddy.product.Product;
import com.beautybuddy.product.ProductDTO;
import com.beautybuddy.product.ProductShade;
import com.beautybuddy.product.ProductShadeDTO;

import java.util.Comparator;
import java.util.List;

public class DTOMapper {

    public static BrandDTO toBrandDTO(Brand brand) {
        return new BrandDTO(brand.getBrandId(), brand.getName());
    }

    public static CategoryDTO toCategoryDTO(Category category) {
        return new CategoryDTO(category.getCategoryId(), category.getName());
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
            .sorted(Comparator.comparing(ProductShade::getShadeNumber))
            .map(DTOMapper::toProductShadeDTO)
            .toList();

        return new ProductDTO(
                product.getProductId(),
                product.getName(),
                toBrandDTO(product.getBrand()),
                toCategoryDTO(product.getCategory()),
                product.getImageLink(),
                product.getProductLink(),
                product.getPrice(),
                product.getRating(),
                ingredients,
                mayContain,
                shades
        );
    }
}