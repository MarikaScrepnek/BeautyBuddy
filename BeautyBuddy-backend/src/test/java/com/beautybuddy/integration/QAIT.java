package com.beautybuddy.integration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
public class QAIT extends BaseIntegrationTest {

    @Test
    void createQuestion_success() throws Exception {
        String email = registerUser("questionuser");
        Long productId = getAnyProductId();
        String request = """
        {
            "productId": %d,
            "text": "What is the best moisturizer for dry skin?"
        }
        """.formatted(productId);

        mockMvc.perform(post("/api/questions/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void getQuestionsForProduct_success() throws Exception {
        String email = registerUser("getquestionuser");
        String questionId = createQuestionAndGetId(email);
        Long productId = findProductIdForQuestion(Long.parseLong(questionId));

        mockMvc.perform(get("/api/questions/" + productId)
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void searchQuestions_success() throws Exception {
        String email = registerUser("getquestionsuser");
        Long productId = getAnyProductId();
        String marker = "qa-search-" + System.nanoTime();
        String request = """
        {
          "productId": %d,
          "text": "%s"
        }
        """.formatted(productId, marker);

        mockMvc.perform(post("/api/questions/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/questions/" + productId + "/search")
                        .param("query", marker)
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk())
                .andExpect(status().isOk());
    }

    @Test
    void editQuestion_success() throws Exception {
        String email = registerUser("editquestionuser");
        String questionId = createQuestionAndGetId(email);
        String request = """
        {
          "questionId": %s,
          "text": "Updated question text"
        }
        """.formatted(questionId);

        mockMvc.perform(post("/api/questions/" + questionId + "/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void editQuestion_unauthorized() throws Exception {
        String request = """
        {
          "questionId": 1,
          "text": "Updated question text"
        }
        """;

        mockMvc.perform(post("/api/questions/1/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteQuestion_success() throws Exception {
        String email = registerUser("deletequestionuser");
        String questionId = createQuestionAndGetId(email);
        mockMvc.perform(delete("/api/questions/" + questionId)
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteQuestion_unauthorized() throws Exception {
        mockMvc.perform(delete("/api/questions/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createAnswer_success() throws Exception {
        String email = registerUser("answeruser");
        String questionId = createQuestionAndGetId(email);
        String request = """
        {
          "questionId": %s,
          "text": "I recommend using a moisturizer with hyaluronic acid, like Neutrogena Hydro Boost."
        }
        """.formatted(questionId);

        mockMvc.perform(post("/api/answers/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void editAnswer_success() throws Exception {
        String email = registerUser("editansweruser");
        String questionId = createQuestionAndGetId(email);
        String answerId = createAnswerAndGetId(email, questionId);
        String request = """
        {
          "answerId": %s,
          "text": "Updated answer content"
        }
        """.formatted(answerId);

        mockMvc.perform(post("/api/answers/" + answerId + "/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void editAnswer_unauthorized() throws Exception {
        String request = """
        {
          "answerId": 1,
          "text": "Updated answer content"
        }
        """;

        mockMvc.perform(post("/api/answers/1/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteAnswer_success() throws Exception {
        String email = registerUser("deleteansweruser");
        String questionId = createQuestionAndGetId(email);
        String answerId = createAnswerAndGetId(email, questionId);
        mockMvc.perform(delete("/api/answers/" + answerId)
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAnswer_unauthorized() throws Exception {
        mockMvc.perform(delete("/api/answers/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void upvoteQuestion_success() throws Exception {
        String email = registerUser("upvoteuser");
        String questionId = createQuestionAndGetId(email);
        mockMvc.perform(post("/api/questions/" + questionId + "/upvote")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void upvoteQuestion_unauthorized() throws Exception {
        mockMvc.perform(post("/api/questions/1/upvote"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removeUpvoteQuestion_success() throws Exception {
        String email = registerUser("removeupvoteuser");
        String questionId = createQuestionAndGetId(email);
        mockMvc.perform(post("/api/questions/" + questionId + "/upvote")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/questions/" + questionId + "/upvote")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void upvoteAnswer_success() throws Exception {
        String email = registerUser("upvoteansweruser");
        String questionId = createQuestionAndGetId(email);
        String answerId = createAnswerAndGetId(email, questionId);
        mockMvc.perform(post("/api/answers/" + answerId + "/upvote")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void upvoteAnswer_unauthorized() throws Exception {
        mockMvc.perform(post("/api/answers/1/upvote"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removeUpvoteAnswer_success() throws Exception {
        String email = registerUser("removeupvoteansweruser");
        String questionId = createQuestionAndGetId(email);
        String answerId = createAnswerAndGetId(email, questionId);
        mockMvc.perform(post("/api/answers/" + answerId + "/upvote")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/answers/" + answerId + "/upvote")
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void reportQuestion_success() throws Exception {
        String email = registerUser("reportquestionuser");
        String questionId = createQuestionAndGetId(email);
        String request = """
        {
          "targetType": "question",
          "targetId": %s,
          "reason": "Inappropriate content"
        }
        """.formatted(questionId);

        mockMvc.perform(post("/api/questions/" + questionId + "/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }

    @Test
    void reportAnswer_success() throws Exception {
        String email = registerUser("reportansweruser");
        String questionId = createQuestionAndGetId(email);
        String answerId = createAnswerAndGetId(email, questionId);
        String request = """
        {
                                        "targetType": "answer",
                                        "targetId": %s,
          "reason": "Inappropriate content"
        }
                                """.formatted(answerId);

        mockMvc.perform(post("/api/answers/" + answerId + "/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                                                                                                .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());
    }
}
