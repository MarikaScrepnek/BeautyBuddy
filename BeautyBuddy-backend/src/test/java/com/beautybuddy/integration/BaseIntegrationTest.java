package com.beautybuddy.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
public abstract class BaseIntegrationTest {

    static {
        // Keep secrets out of source code: read local .env only if process env is not set.
        if (isBlank(System.getenv("JWT_SECRET_KEY")) && isBlank(System.getProperty("JWT_SECRET_KEY"))) {
            String fromEnvFile = loadFromDotEnv("JWT_SECRET_KEY");
            if (!isBlank(fromEnvFile)) {
                System.setProperty("JWT_SECRET_KEY", fromEnvFile);
            }
        }
    }

        // Managed by JUnit 5 Testcontainers extension; lifecycle is not manual here.
        @SuppressWarnings("resource")
        @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("beautybuddy_test")
                    .withUsername("beautybuddy")
                    .withPassword("beautybuddy");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

        protected String registerUser(String usernamePrefix) throws Exception {
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

        protected MvcResult login(String email, String password) throws Exception {
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

        protected String loginAndGetJwt(String email) throws Exception {
                requireJwtSecret();
                MvcResult loginResult = login(email, "password123");
                Cookie jwtCookie = loginResult.getResponse().getCookie("jwt");
                Assertions.assertNotNull(jwtCookie, "Expected JWT cookie to be present after login");
                return jwtCookie.getValue();
        }

        protected String createQuestionAndGetId(String email) throws Exception {
                String request = """
                {
                    "title": "Test question",
                    "content": "Question content"
                }
                """;

                MvcResult result = mockMvc.perform(post("/api/questions")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(request)
                                                .cookie(new Cookie("jwt", loginAndGetJwt(email))))
                                .andExpect(status().isCreated())
                                .andReturn();

                return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
        }

        protected String createAnswerAndGetId(String email, String questionId) throws Exception {
                String request = """
                {
                    "content": "Test answer"
                }
                """;

                MvcResult result = mockMvc.perform(post("/api/questions/" + questionId + "/answers")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(request)
                                                .cookie(new Cookie("jwt", loginAndGetJwt(email))))
                                .andExpect(status().isCreated())
                                .andReturn();

                return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
        }

        protected String uniqueEmail() {
                return "user" + System.nanoTime() + "@example.com";
        }

        protected void requireJwtSecret() {
                String jwtSecret = System.getenv("JWT_SECRET_KEY");
                if (jwtSecret == null || jwtSecret.isBlank()) {
                        jwtSecret = System.getProperty("JWT_SECRET_KEY");
                }
                Assertions.assertTrue(jwtSecret != null && !jwtSecret.isBlank(),
                                "JWT_SECRET_KEY is required for JWT integration tests");
        }

    private static String loadFromDotEnv(String key) {
        List<Path> candidates = List.of(
                Path.of(".env"),
                Path.of("..", ".env"),
                Path.of("..", "..", ".env")
        );

        for (Path candidate : candidates) {
            if (!Files.exists(candidate)) {
                continue;
            }
            try {
                for (String line : Files.readAllLines(candidate)) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                        continue;
                    }
                    if (trimmed.startsWith("export ")) {
                        trimmed = trimmed.substring(7).trim();
                    }
                    int eq = trimmed.indexOf('=');
                    if (eq <= 0) {
                        continue;
                    }
                    String k = trimmed.substring(0, eq).trim();
                    if (!key.equals(k)) {
                        continue;
                    }
                    String value = trimmed.substring(eq + 1).trim();
                    if ((value.startsWith("\"") && value.endsWith("\""))
                            || (value.startsWith("'") && value.endsWith("'"))) {
                        value = value.substring(1, value.length() - 1);
                    }
                    return value;
                }
            } catch (IOException ignored) {
                // Ignore unreadable local .env files and keep default behavior.
            }
        }
        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
