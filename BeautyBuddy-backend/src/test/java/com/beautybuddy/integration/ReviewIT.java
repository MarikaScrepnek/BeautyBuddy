package com.beautybuddy.integration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
public class ReviewIT extends BaseIntegrationTest {
    
    @Test
    void createReview_success() throws Exception {
        String email = registerUser("reviewer");
        Long productId = getAnyProductId();
        String request = """
        {
            "productId": %d,
            "rating": 5,
            "title": "Great product",
            "text": "Great product!"
        }
        """.formatted(productId);

        mockMvc.perform(post("/api/reviews/add")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
    }

    @Test
    void createReview_unauthorized() throws Exception {
        Long productId = getAnyProductId();
        String request = """
        {
            "productId": %d,
            "rating": 5,
            "title": "Great product",
            "text": "Great product!"
        }
        """.formatted(productId);

        mockMvc.perform(post("/api/reviews/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void editReview_success() throws Exception {
        String email = registerUser("reviewer");
        Long reviewId = createReviewAndGetId(email);
        String request = """
        {
            "shadeName": null,
            "rating": 4,
            "title": "Updated title",
            "text": "Good product, but could be better.",
            "imageLinks": []
        }
        """;

        mockMvc.perform(post("/api/reviews/" + reviewId + "/edit")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
    }

    @Test
    void editReview_unauthorized() throws Exception {
        String request = """
        {
            "shadeName": null,
            "rating": 4,
            "title": "Updated title",
            "text": "Good product, but could be better.",
            "imageLinks": []
        }
        """;

        mockMvc.perform(post("/api/reviews/1/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteReview_success() throws Exception {
        String email = registerUser("reviewer");
        Long reviewId = createReviewAndGetId(email);

        mockMvc.perform(delete("/api/reviews/" + reviewId)
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteReview_unauthorized() throws Exception {
        mockMvc.perform(delete("/api/reviews/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void upvoteReview_success() throws Exception {
        String email = registerUser("upvoter");
        Long reviewId = createReviewAndGetId(email);

        mockMvc.perform(post("/api/reviews/" + reviewId + "/upvote")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void upvoteReview_unauthorized() throws Exception {
        mockMvc.perform(post("/api/reviews/1/upvote"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removeUpvoteReview_success() throws Exception {
        String email = registerUser("upvoter");
        Long reviewId = createReviewAndGetId(email);
        mockMvc.perform(post("/api/reviews/" + reviewId + "/upvote")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/reviews/" + reviewId + "/upvote")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void reportReview_success() throws Exception {
        String email = registerUser("reporter");
        Long reviewId = createReviewAndGetId(email);
        String request = """
        {
            "reason": "Inappropriate content"
        }
        """;

        mockMvc.perform(post("/api/reviews/" + reviewId + "/report")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
    }
    
}
