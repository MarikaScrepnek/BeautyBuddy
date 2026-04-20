package com.beautybuddy.integration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
public class AuthIT extends BaseIntegrationTest {

    @Test
    void registerUser_success() throws Exception {
        String email = uniqueEmail();
        String request = """
        {
          "username": "testuser",
          "email": "%s",
          "password": "password123"
        }
        """.formatted(email);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void login_success_setsJwtCookie() throws Exception {
        requireJwtSecret();
        String email = registerUser("loginuser");

        String request = """
        {
          "email": "%s",
          "password": "password123"
        }
        """.formatted(email);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(header().string("Set-Cookie", containsString("jwt=")));
    }

    @Test
    void login_invalidCredentials_returnsUnauthorized() throws Exception {
        requireJwtSecret();
        String request = """
        {
          "email": "wrong@example.com",
          "password": "wrong"
        }
        """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_withoutCookie_returnsEmptyBody() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void me_withCookie_returnsCurrentUser() throws Exception {
        requireJwtSecret();
        String email = registerUser("meuser");
        MvcResult loginResult = login(email, "password123");
      Cookie jwtCookie = loginResult.getResponse().getCookie("jwt");
      Assertions.assertNotNull(jwtCookie, "Expected JWT cookie to be present after login");

        mockMvc.perform(get("/api/auth/me")
          .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    private String registerUser(String usernamePrefix) throws Exception {
        String email = uniqueEmail();
        String request = """
        {
          "username": "%s",
          "email": "%s",
          "password": "password123"
        }
        """.formatted(usernamePrefix + System.nanoTime(), email);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        return email;
    }

    private MvcResult login(String email, String password) throws Exception {
        String request = """
        {
          "email": "%s",
          "password": "%s"
        }
        """.formatted(email, password);

        return mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn();
    }

    private String uniqueEmail() {
        return "user" + System.nanoTime() + "@example.com";
    }

    private void requireJwtSecret() {
        String jwtSecret = System.getenv("JWT_SECRET_KEY");
        if (jwtSecret == null || jwtSecret.isBlank()) {
            jwtSecret = System.getProperty("JWT_SECRET_KEY");
        }
        Assertions.assertTrue(jwtSecret != null && !jwtSecret.isBlank(),
                "JWT_SECRET_KEY is required for JWT integration tests");
    }
}