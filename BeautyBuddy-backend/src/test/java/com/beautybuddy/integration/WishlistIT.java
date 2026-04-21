package com.beautybuddy.integration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
public class WishlistIT extends BaseIntegrationTest {

    @Test
    void addAndGetWishlist_success() throws Exception {
        String email = registerUser("wishlistuser");
        Long productId = getAnyProductId();
        addToWishlist(email, productId, null);

        mockMvc.perform(get("/api/wishlist")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void searchWishlist_success() throws Exception {
        String email = registerUser("wishlistsearchuser");
        Long productId = getAnyProductId();
        String productName = getAnyProductName();
        String query = productName.length() > 4 ? productName.substring(0, 4) : productName;

        addToWishlist(email, productId, null);

        mockMvc.perform(get("/api/wishlist/search")
                        .param("query", query)
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void getWishlist_withQueryFilter_success() throws Exception {
        String email = registerUser("wishlistfilteruser");
        Long productId = getAnyProductId();
        String productName = getAnyProductName();
        String query = productName.length() > 4 ? productName.substring(0, 4) : productName;

        addToWishlist(email, productId, null);

        mockMvc.perform(get("/api/wishlist")
                        .param("query", query)
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void sortWishlist_success() throws Exception {
        String email = registerUser("wishlistsortuser");
        Long productId = getAnyProductId();
        addToWishlist(email, productId, null);

        mockMvc.perform(get("/api/wishlist/sort")
                        .param("type", "added_desc")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void removeFromWishlist_success() throws Exception {
        String email = registerUser("wishlistremoveuser");
        Long productId = getAnyProductId();
        addToWishlist(email, productId, null);

        String request = """
        {
          "productId": %d,
          "shadeName": null
        }
        """.formatted(productId);

        mockMvc.perform(post("/api/wishlist/remove")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
    }

    @Test
    void wishlist_unauthorized() throws Exception {
        mockMvc.perform(get("/api/wishlist"))
                .andExpect(status().isUnauthorized());
    }
}
