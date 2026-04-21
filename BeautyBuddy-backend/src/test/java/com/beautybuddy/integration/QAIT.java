package com.beautybuddy.integration;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
public class QAIT extends BaseIntegrationTest{
    
    @Test
    void createQuestion_success() throws Exception {
        String email = registerUser("questionuser");
        String request = """
        {
          "title": "What is the best moisturizer for dry skin?",
          "content": "I have very dry skin and I'm looking for recommendations on the best moisturizer to use. Any suggestions?"
        }
        """;
        mockMvc.perform(post("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .cookie(new Cookie("jwt", loginAndGetJwt(email))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("What is the best moisturizer for dry skin?"))
                .andExpect(jsonPath("$.content").value("I have very dry skin and I'm looking for recommendations on the best moisturizer to use. Any suggestions?"));
    }

    @Test
    void getQuestion_success() throws Exception {
        String email = registerUser("getquestionuser");
        String questionId = createQuestionAndGetId(email);
        mockMvc.perform(get("/api/questions/" + questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(questionId));
    }

    @Test
    void getQuestions_success() throws Exception {
        String email = registerUser("getquestionsuser");
        createQuestionAndGetId(email);
        createQuestionAndGetId(email);
        mockMvc.perform(get("/api/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void editQuestion_success() throws Exception {
        String email = registerUser("editquestionuser");
        String questionId = createQuestionAndGetId(email);
        String request = """
        {
          "title": "Updated title",
          "content": "Updated content"
        }
        """;
        mockMvc.perform(put("/api/questions/" + questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .cookie(new Cookie("jwt", loginAndGetJwt(email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    void editQuestion_unauthorized() throws Exception {
        String email = registerUser("unauthorizededituser");
        String questionId = createQuestionAndGetId(email);
        String request = """
        {
          "title": "Updated title",
          "content": "Updated content"
        }
        """;
        mockMvc.perform(put("/api/questions/" + questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteQuestion_success() throws Exception {
        String email = registerUser("deletequestionuser");
        String questionId = createQuestionAndGetId(email);
        mockMvc.perform(delete("/api/questions/" + questionId)
                        .cookie(new Cookie("jwt", loginAndGetJwt(email))))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteQuestion_unauthorized() throws Exception {
        String email = registerUser("unauthorizeddeleteuser");
        String questionId = createQuestionAndGetId(email);
        mockMvc.perform(delete("/api/questions/" + questionId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createAnswer_success() throws Exception {
        String email = registerUser("answeruser");
        String questionId = createQuestionAndGetId(email);
        String request = """
        {
          "content": "I recommend using a moisturizer with hyaluronic acid, like Neutrogena Hydro Boost."
        }
        """;
        mockMvc.perform(post("/api/questions/" + questionId + "/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .cookie(new Cookie("jwt", loginAndGetJwt(email))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("I recommend using a moisturizer with hyaluronic acid, like Neutrogena Hydro Boost."));
    }

    @Test
    void getAnswer_success() throws Exception {
        String email = registerUser("getansweruser");
        String questionId = createQuestionAndGetId(email);
        String answerId = createAnswerAndGetId(email, questionId);
        mockMvc.perform(get("/api/answers/" + answerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(answerId));
    }

    @Test
    void getAnswersByQuestion_success() throws Exception {
        String email = registerUser("getanswersbyquestionuser");
        String questionId = createQuestionAndGetId(email);
        createAnswerAndGetId(email, questionId);
        createAnswerAndGetId(email, questionId);
        mockMvc.perform(get("/api/questions/" + questionId + "/answers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void editAnswer_success() throws Exception {
        String email = registerUser("editansweruser");
        String questionId = createQuestionAndGetId(email);
        String answerId = createAnswerAndGetId(email, questionId);
        String request = """
        {
          "content": "Updated answer content"
        }
        """;
        mockMvc.perform(put("/api/answers/" + answerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .cookie(new Cookie("jwt", loginAndGetJwt(email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated answer content"));
    }

    @Test
    void editAnswer_unauthorized() throws Exception {
        String email = registerUser("unauthorizededitansweruser");
        String questionId = createQuestionAndGetId(email);
        String answerId = createAnswerAndGetId(email, questionId);
        String request = """
        {
          "content": "Updated answer content"
        }
        """;
        mockMvc.perform(put("/api/answers/" + answerId)
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
                        .cookie(new Cookie("jwt", loginAndGetJwt(email))))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAnswer_unauthorized() throws Exception {
        String email = registerUser("unauthorizeddeleteansweruser");
        String questionId = createQuestionAndGetId(email);
        String answerId = createAnswerAndGetId(email, questionId);
        mockMvc.perform(delete("/api/answers/" + answerId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void upvoteQuestion_success() throws Exception {
        String email = registerUser("upvoteuser");
        String questionId = createQuestionAndGetId(email);
        mockMvc.perform(post("/api/questions/" + questionId + "/upvote")
                        .cookie(new Cookie("jwt", loginAndGetJwt(email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.upvotes").value(1));
    }

    @Test
    void upvoteQuestion_unauthorized() throws Exception {
        String email = registerUser("unauthorizedupvoteuser");
        String questionId = createQuestionAndGetId(email);
        mockMvc.perform(post("/api/questions/" + questionId + "/upvote"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removeUpvoteQuestion_success() throws Exception {
        String email = registerUser("removeupvoteuser");
        String questionId = createQuestionAndGetId(email);
        mockMvc.perform(post("/api/questions/" + questionId + "/upvote")
                        .cookie(new Cookie("jwt", loginAndGetJwt(email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.upvotes").value(1));
        mockMvc.perform(delete("/api/questions/" + questionId + "/upvote")
                        .cookie(new Cookie("jwt", loginAndGetJwt(email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.upvotes").value(0));
    }

    @Test
    void upvoteAnswer_success() throws Exception {
        String email = registerUser("upvoteansweruser");
        String questionId = createQuestionAndGetId(email);
        String answerId = createAnswerAndGetId(email, questionId);
        mockMvc.perform(post("/api/answers/" + answerId + "/upvote")
                        .cookie(new Cookie("jwt", loginAndGetJwt(email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.upvotes").value(1));
    }

    @Test
    void upvoteAnswer_unauthorized() throws Exception {
        String email = registerUser("unauthorizedupvoteansweruser");
        String questionId = createQuestionAndGetId(email);
        String answerId = createAnswerAndGetId(email, questionId);
        mockMvc.perform(post("/api/answers/" + answerId + "/upvote"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removeUpvoteAnswer_success() throws Exception {
        String email = registerUser("removeupvoteansweruser");
        String questionId = createQuestionAndGetId(email);
        String answerId = createAnswerAndGetId(email, questionId);
        mockMvc.perform(post("/api/answers/" + answerId + "/upvote")
                        .cookie(new Cookie("jwt", loginAndGetJwt(email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.upvotes").value(1));
        mockMvc.perform(delete("/api/answers/" + answerId + "/upvote")
                        .cookie(new Cookie("jwt", loginAndGetJwt(email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.upvotes").value(0));
    }
    

    @Test
    void reportQuestion_success() throws Exception {
        String email = registerUser("reportquestionuser");
        String questionId = createQuestionAndGetId(email);
        String request = """
        {
          "reason": "Inappropriate content"
        }
        """;
        mockMvc.perform(post("/api/questions/" + questionId + "/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .cookie(new Cookie("jwt", loginAndGetJwt(email))))
                .andExpect(status().isOk());
    }

    @Test
    void reportAnswer_success() throws Exception {
        String email = registerUser("reportansweruser");
        String questionId = createQuestionAndGetId(email);
        String answerId = createAnswerAndGetId(email, questionId);
        String request = """
        {
          "reason": "Inappropriate content"
        }
        """;
        mockMvc.perform(post("/api/answers/" + answerId + "/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .cookie(new Cookie("jwt", loginAndGetJwt(email))))
                .andExpect(status().isOk());
    }
}
