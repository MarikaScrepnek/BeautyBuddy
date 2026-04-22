package com.beautybuddy.ingredient;

import com.beautybuddy.common.DTOMapper;
import com.beautybuddy.ingredient.dto.IngredientDTO;
import com.beautybuddy.ingredient.repo.IngredientRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientRepository ingredientRepository;

    public IngredientController(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @GetMapping
    public Page<IngredientDTO> getIngredients(Pageable pageable, @RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            Page<IngredientDTO> canonicalMatches = ingredientRepository
                .findByCanonicalIdNullAndNameContaining(pageable, search)
                .map(DTOMapper::toIngredientDTO);

            if (!canonicalMatches.isEmpty()) {
                return canonicalMatches;
            }

            // If no canonical ingredients match, try non-canonical ones and
            // return their canonical ingredient with the non-canonical name in brackets.
            return ingredientRepository
                .findByCanonicalIdNotNullAndNameContaining(pageable, search)
                .map(nonCanonical -> {
                    Long canonicalId = nonCanonical.getCanonicalId();
                    // Fallback to nonCanonical itself if canonical not found
                    var canonicalOpt = canonicalId != null
                        ? ingredientRepository.findById(canonicalId)
                        : java.util.Optional.of(nonCanonical);

                    var canonical = canonicalOpt.orElse(nonCanonical);
                    String displayName;
                    if (canonicalId != null && !canonical.getName().equalsIgnoreCase(nonCanonical.getName())) {
                        displayName = canonical.getName() + " (" + nonCanonical.getName() + ")";
                    } else {
                        displayName = canonical.getName();
                    }

                    return new IngredientDTO(canonical.getId(), displayName);
                });
        }
        return ingredientRepository.findByCanonicalIdNull(pageable)
            .map(DTOMapper::toIngredientDTO);
    }
}
