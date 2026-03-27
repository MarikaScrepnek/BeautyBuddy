package com.beautybuddy.common;

import com.beautybuddy.brand.Brand;
import com.beautybuddy.brand.BrandDTO;
import com.beautybuddy.breakout.dto.DisplayBreakoutListProductDTO;
import com.beautybuddy.breakout.entity.BreakoutListProduct;
import com.beautybuddy.category.Category;
import com.beautybuddy.category.CategoryDTO;
import com.beautybuddy.ingredient.dto.IngredientDTO;
import com.beautybuddy.ingredient.dto.MayContainIngredientDTO;
import com.beautybuddy.ingredient.dto.ProductIngredientDTO;
import com.beautybuddy.ingredient.entity.Ingredient;
import com.beautybuddy.ingredient.entity.MayContainIngredient;
import com.beautybuddy.ingredient.entity.ProductIngredient;
import com.beautybuddy.product.dto.ProductDTO;
import com.beautybuddy.product.dto.ProductShadeDTO;
import com.beautybuddy.product.entity.Product;
import com.beautybuddy.product.entity.ProductShade;
import com.beautybuddy.review.ReviewRepository;
import com.beautybuddy.review.entity.Review;
import com.beautybuddy.routine.dto.DisplayRoutineDTO;
import com.beautybuddy.routine.dto.DisplayRoutineItemDTO;
import com.beautybuddy.routine.entity.Routine;
import com.beautybuddy.routine.entity.RoutineItem;
import com.beautybuddy.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DTOMapper {

    public static BrandDTO toBrandDTO(Brand brand) {
        return new BrandDTO(brand.getId(), brand.getName());
    }

    public static CategoryDTO toCategoryDTO(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryDTO(category.getId(), category.getName(), category.getBaseCategory().getName());
    }

    public static IngredientDTO toIngredientDTO(Ingredient ingredient) {
        return new IngredientDTO(ingredient.getId(), ingredient.getName());
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
                ps.getHexCode(),
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
                product.getId(),
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

    public static DisplayRoutineDTO toDisplayRoutineDTO(Routine routine, ReviewRepository reviewRepository) {
        User user = routine.getUser();
        LocalDateTime now = LocalDateTime.now();

        List<RoutineItem> validItems = routine.getItems().stream()
            .filter(item -> item.getValidFrom().isBefore(now) && (item.getValidTo() == null || item.getValidTo().isAfter(now)))
            .sorted(Comparator.comparingInt(RoutineItem::getStepOrder))
            .toList();

        List<DisplayRoutineItemDTO> itemDTOs = new ArrayList<>();
        for (RoutineItem item : validItems) {
            BigDecimal rating = null;
            if (item.getShade() != null) {
                rating = reviewRepository
                    .findByProduct_IdAndProductShade_IdAndUser_IdAndDeletedAtIsNull(
                        item.getProduct().getId(),
                        item.getShade().getId(),
                        user.getId()
                    )
                    .map(Review::getRating)
                    .orElse(null);
            }
            if (item.getShade() == null) {
                rating = reviewRepository
                    .findByProduct_IdAndUser_IdAndDeletedAtIsNull(
                        item.getProduct().getId(),
                        user.getId()
                    )
                    .map(Review::getRating)
                    .orElse(null);
            }

            DisplayRoutineItemDTO dto = new DisplayRoutineItemDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getBrand().getName(),
                item.getShade() != null ? item.getShade().getShadeName() : null,
                item.getProduct().getProductShades().stream()
                    .sorted(Comparator.comparing(ProductShade::getShadeNumber))
                    .map(DTOMapper::toProductShadeDTO)
                    .toList(),
                item.getProduct().getCategory().getName(),
                item.getShade() != null && item.getShade().getImageLink() != null
                    ? item.getShade().getImageLink()
                    : item.getProduct().getImageLink(),
                item.getNotes(),
                rating,
                item.getShade() != null
                    ? reviewRepository.findByProduct_IdAndProductShade_IdAndUser_IdAndDeletedAtIsNull(
                        item.getProduct().getId(),
                        item.getShade().getId(),
                        user.getId()
                    )
                    .map(Review::getId)
                    .orElse(null)
                    : reviewRepository.findByProduct_IdAndUser_IdAndDeletedAtIsNull(
                        item.getProduct().getId(),
                        user.getId()
                    )
                    .map(Review::getId)
                    .orElse(null),
                item.getStepOrder()
            );

            itemDTOs.add(dto);
        }

        return new DisplayRoutineDTO(
            routine.getId(),
            routine.getName(),
            user.getUsername(),
            routine.getUpdatedAt(),
            routine.getTimeOfDay() != null ? routine.getTimeOfDay().name() : null,
            routine.getOccasion() != null ? routine.getOccasion().name() : null,
            routine.getNotes(),
            itemDTOs
        );
    }

    public static DisplayBreakoutListProductDTO toDisplayBreakoutListProductDTO(BreakoutListProduct blp) {
        return new DisplayBreakoutListProductDTO(
            blp.getId(),
            blp.getProduct().getId(),
            blp.getProduct().getName(),
            blp.getProduct().getBrand().getName(),
            blp.getProduct().getImageLink(),
            blp.getProduct().getProductIngredients().stream()
                .sorted(Comparator.comparingInt(ProductIngredient::getPosition))
                .map(pi -> toIngredientDTO(pi.getIngredient()))
                .toList()
        );
    }
}