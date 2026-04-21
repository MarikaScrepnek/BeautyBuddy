package com.beautybuddy.integration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
public class ProductIT extends BaseIntegrationTest {

    @Test
    void getAllProducts_success() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }

    @Test
    void getProductById_success() throws Exception {
        Long productId = getAnyProductId();

        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk());
    }

    @Test
    void searchProducts_success() throws Exception {
        String productName = getAnyProductName();
        String query = productName.length() > 4 ? productName.substring(0, 4) : productName;

        mockMvc.perform(get("/api/products/search")
                        .param("q", query)
                        .param("sort", "added_desc"))
                .andExpect(status().isOk());
    }

    @Test
    void getProductIngredients_success() throws Exception {
        Long productId = getAnyProductId();

        mockMvc.perform(get("/api/products/" + productId + "/ingredients"))
                .andExpect(status().isOk());
    }

    @Test
    void getProductMayContain_success() throws Exception {
        Long productId = getAnyProductId();

        mockMvc.perform(get("/api/products/" + productId + "/maycontain"))
                .andExpect(status().isOk());
    }

    @Test
    void reportProduct_success() throws Exception {
        String email = registerUser("productreporter");
        Long productId = getAnyProductId();
        String request = """
        {
          "reason": "Inappropriate product listing"
        }
        """;

        mockMvc.perform(post("/api/products/" + productId + "/report")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
    }

    @Test
    void reportProduct_unauthorized() throws Exception {
        Long productId = getAnyProductId();
        String request = """
        {
          "reason": "Inappropriate product listing"
        }
        """;

        mockMvc.perform(post("/api/products/" + productId + "/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }
}
