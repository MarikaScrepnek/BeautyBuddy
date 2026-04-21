package com.beautybuddy.integration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
public class BreakoutListIT extends BaseIntegrationTest {

    @Test
    void addProductAndGetBreakoutProducts_success() throws Exception {
        String email = registerUser("breakoutproductuser");
        Long productId = getAnyProductId();

        addProductToBreakoutList(email, productId);

        mockMvc.perform(get("/api/breakout-list/products")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void addIngredientAndGetBreakoutIngredients_success() throws Exception {
        String email = registerUser("breakoutingredientuser");
        Long ingredientId = getAnyIngredientId();

        addIngredientToBreakoutList(email, ingredientId);

        mockMvc.perform(get("/api/breakout-list/ingredients")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void removeProductFromBreakoutList_success() throws Exception {
        String email = registerUser("breakoutremoveproductuser");
        Long productId = getAnyProductId();

        addProductToBreakoutList(email, productId);
        removeFromBreakoutList(email, productId, null);

        mockMvc.perform(get("/api/breakout-list/products")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void removeIngredientFromBreakoutList_success() throws Exception {
        String email = registerUser("breakoutremoveingredientuser");
        Long ingredientId = getAnyIngredientId();

        addIngredientToBreakoutList(email, ingredientId);
        removeFromBreakoutList(email, null, ingredientId);

        mockMvc.perform(get("/api/breakout-list/ingredients")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }
}
