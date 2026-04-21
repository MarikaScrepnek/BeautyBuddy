package com.beautybuddy.integration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
public class RoutineIT extends BaseIntegrationTest {

    @Test
    void createAndGetMakeupRoutine_success() throws Exception {
        String email = registerUser("routineuser");
        createMakeupRoutineAndGetId(email);

        mockMvc.perform(get("/api/routines/makeup")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void addProductToRoutineAndGetItems_success() throws Exception {
        String email = registerUser("routineitemsuser");
        Long routineId = createMakeupRoutineAndGetId(email);
        Long productId = getAnyProductId();

        addProductToRoutine(email, routineId, productId, null);

        mockMvc.perform(get("/api/routines/items")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void removeProductFromRoutine_success() throws Exception {
        String email = registerUser("routineremoveuser");
        Long routineId = createMakeupRoutineAndGetId(email);
        Long productId = getAnyProductId();

        addProductToRoutine(email, routineId, productId, null);

        mockMvc.perform(delete("/api/routines/" + routineId + "/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void getMakeupRoutines_unauthorized() throws Exception {
        mockMvc.perform(get("/api/routines/makeup"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void searchRoutines_success() throws Exception {
        mockMvc.perform(get("/api/routines/search"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("search query")));
    }

    @Test
    void updateProductInRoutine_placeholderEndpoint_success() throws Exception {
        mockMvc.perform(put("/api/routines/1/update-product"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("update a product")));
    }

    @Test
    void deleteMakeupRoutine_placeholderEndpoint_success() throws Exception {
        mockMvc.perform(delete("/api/routines/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("soft delete")));
    }
}
