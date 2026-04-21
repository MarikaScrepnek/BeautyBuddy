package com.beautybuddy.integration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
public class DiscussionIT extends BaseIntegrationTest {

    @Test
    void createDiscussion_success() throws Exception {
        String email = registerUser("discussionuser");
        String request = """
        {
          "title": "Discussion Title",
          "text": "This is a discussion content."
        }
        """;

        mockMvc.perform(post("/api/discussions")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
    }

    @Test
    void createDiscussion_unauthorized() throws Exception {
        String request = """
        {
          "title": "Discussion Title",
          "text": "This is a discussion content."
        }
        """;

        mockMvc.perform(post("/api/discussions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getDiscussions_success() throws Exception {
        String email = registerUser("discussionuser2");
        createDiscussionAndGetId(email);

        mockMvc.perform(get("/api/discussions")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void searchDiscussions_success() throws Exception {
        String email = registerUser("discussionuser3");
        String marker = "discussion-search-" + System.nanoTime();
        createDiscussionAndGetId(email, marker, marker);

        var result = mockMvc.perform(get("/api/discussions/search")
                        .param("query", marker)
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk())
                .andReturn();

        var root = objectMapper.readTree(result.getResponse().getContentAsString());
        var content = root.get("content");
        Assertions.assertTrue(content != null && content.isArray() && !content.isEmpty(),
                "Expected discussion search to return at least one result");

        boolean found = false;
        for (var discussionNode : content) {
            String title = discussionNode.hasNonNull("title") ? discussionNode.get("title").asText() : "";
            String text = discussionNode.hasNonNull("text") ? discussionNode.get("text").asText() : "";
            if (marker.equals(title) || marker.equals(text)) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Expected discussion search results to contain the created marker");
    }

    @Test
    void editDiscussion_success() throws Exception {
        String email = registerUser("discussionuser4");
        Long discussionId = createDiscussionAndGetId(email);

        String request = """
        {
          "title": "Updated Discussion Title",
          "text": "This is updated discussion content."
        }
        """;

        mockMvc.perform(post("/api/discussions/" + discussionId + "/edit")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
    }

    @Test
    void editDiscussion_unauthorized() throws Exception {
        String request = """
        {
          "title": "Updated Discussion Title",
          "text": "This is updated discussion content."
        }
        """;

        mockMvc.perform(post("/api/discussions/1/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void editDiscussion_notOwner_returnsServerError() throws Exception {
        String email1 = registerUser("discussionuser6");
        String email2 = registerUser("discussionuser7");
        Long discussionId = createDiscussionAndGetId(email1);

        String request = """
        {
          "title": "Updated Discussion Title",
          "text": "This is updated discussion content."
        }
        """;

        mockMvc.perform(post("/api/discussions/" + discussionId + "/edit")
                        .cookie(jwtCookieForEmail(email2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void deleteDiscussion_success() throws Exception {
        String email = registerUser("discussionuser8");
        Long discussionId = createDiscussionAndGetId(email);

        mockMvc.perform(delete("/api/discussions/" + discussionId)
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteDiscussion_unauthorized() throws Exception {
        mockMvc.perform(delete("/api/discussions/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteDiscussion_notOwner_returnsServerError() throws Exception {
        String email1 = registerUser("discussionuser10");
        String email2 = registerUser("discussionuser11");
        Long discussionId = createDiscussionAndGetId(email1);

        mockMvc.perform(delete("/api/discussions/" + discussionId)
                        .cookie(jwtCookieForEmail(email2)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void createComment_success() throws Exception {
        String email = registerUser("commentuser");
        Long discussionId = createDiscussionAndGetId(email);
        String request = """
        {
          "parentDiscussionCommentId": null,
          "text": "This is a comment."
        }
        """;

        mockMvc.perform(post("/api/discussions/" + discussionId + "/comment")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
    }

    @Test
    void editComment_success() throws Exception {
        String email = registerUser("commentuser2");
        Long discussionId = createDiscussionAndGetId(email);
        Long commentId = createDiscussionCommentAndGetId(email, discussionId);

        String request = """
        {
          "parentDiscussionCommentId": null,
          "text": "This is an updated comment."
        }
        """;

        mockMvc.perform(post("/api/discussions/comments/" + commentId + "/edit")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
    }

    @Test
    void editComment_notOwner_returnsServerError() throws Exception {
        String email1 = registerUser("commentuser3");
        String email2 = registerUser("commentuser4");
        Long discussionId = createDiscussionAndGetId(email1);
        Long commentId = createDiscussionCommentAndGetId(email1, discussionId);
        String request = """
        {
          "parentDiscussionCommentId": null,
          "text": "This is an updated comment."
        }
        """;

        mockMvc.perform(post("/api/discussions/comments/" + commentId + "/edit")
                        .cookie(jwtCookieForEmail(email2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void deleteComment_success() throws Exception {
        String email = registerUser("commentuser5");
        Long discussionId = createDiscussionAndGetId(email);
        Long commentId = createDiscussionCommentAndGetId(email, discussionId);

        mockMvc.perform(delete("/api/discussions/" + discussionId + "/comment/" + commentId)
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteComment_notOwner_returnsServerError() throws Exception {
        String email1 = registerUser("commentuser6");
        String email2 = registerUser("commentuser7");
        Long discussionId = createDiscussionAndGetId(email1);
        Long commentId = createDiscussionCommentAndGetId(email1, discussionId);

        mockMvc.perform(delete("/api/discussions/" + discussionId + "/comment/" + commentId)
                        .cookie(jwtCookieForEmail(email2)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void upvoteDiscussion_success() throws Exception {
        String email = registerUser("voteruser");
        Long discussionId = createDiscussionAndGetId(email);

        mockMvc.perform(post("/api/discussions/" + discussionId + "/upvote")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void removeUpvoteDiscussion_success() throws Exception {
        String email = registerUser("voteruser2");
        Long discussionId = createDiscussionAndGetId(email);

        mockMvc.perform(post("/api/discussions/" + discussionId + "/upvote")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/discussions/" + discussionId + "/upvote")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void upvoteDiscussion_unauthorized() throws Exception {
        String email = registerUser("voteruser3");
        Long discussionId = createDiscussionAndGetId(email);

        mockMvc.perform(post("/api/discussions/" + discussionId + "/upvote"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void upvoteComment_success() throws Exception {
        String email = registerUser("voteruser4");
        Long discussionId = createDiscussionAndGetId(email);
        Long commentId = createDiscussionCommentAndGetId(email, discussionId);

        mockMvc.perform(post("/api/discussions/comments/" + commentId + "/upvote")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void upvoteComment_unauthorized() throws Exception {
        String email = registerUser("voteruser5");
        Long discussionId = createDiscussionAndGetId(email);
        Long commentId = createDiscussionCommentAndGetId(email, discussionId);

        mockMvc.perform(post("/api/discussions/comments/" + commentId + "/upvote"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removeUpvoteComment_success() throws Exception {
        String email = registerUser("voteruser7");
        Long discussionId = createDiscussionAndGetId(email);
        Long commentId = createDiscussionCommentAndGetId(email, discussionId);

        mockMvc.perform(post("/api/discussions/comments/" + commentId + "/upvote")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/discussions/comments/" + commentId + "/upvote")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void reportDiscussion_success() throws Exception {
        String email = registerUser("reporteruser");
        Long discussionId = createDiscussionAndGetId(email);
        String request = """
        {
          "reason": "Inappropriate content"
        }
        """;

        mockMvc.perform(post("/api/discussions/" + discussionId + "/report")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
    }

    @Test
    void reportComment_success() throws Exception {
        String email = registerUser("reporteruser2");
        Long discussionId = createDiscussionAndGetId(email);
        Long commentId = createDiscussionCommentAndGetId(email, discussionId);

        String request = """
        {
          "reason": "Inappropriate content"
        }
        """;

        mockMvc.perform(post("/api/discussions/comments/" + commentId + "/report")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
    }
}
