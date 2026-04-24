package com.beautybuddy.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URLEncoder;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 5432;
    private static final String DEFAULT_DB_USER = "postgres";
    private static final String DEFAULT_DB_PASSWORD = "postgres";
    private static final String TEST_DB_NAME = "beautybuddy_it_" + System.currentTimeMillis();
    private static final boolean USE_TESTCONTAINERS = isDockerAvailableForTestcontainers();

    static {
        configureWindowsDockerHostForTestcontainers();

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
        if (USE_TESTCONTAINERS) {
            POSTGRES.start();
            registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
            registry.add("spring.datasource.username", POSTGRES::getUsername);
            registry.add("spring.datasource.password", POSTGRES::getPassword);
        } else {
            ensureComposeDbIsReachable();
            String dbUser = valueOrDefault("POSTGRES_USER", DEFAULT_DB_USER);
            String dbPassword = valueOrDefault("POSTGRES_PASSWORD", DEFAULT_DB_PASSWORD);
            ensureIsolatedTestDatabase(dbUser, dbPassword, TEST_DB_NAME);

            registry.add("spring.datasource.url", () -> "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + TEST_DB_NAME);
            registry.add("spring.datasource.username", () -> dbUser);
            registry.add("spring.datasource.password", () -> dbPassword);
        }
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @BeforeEach
    void flushRedis() {
        try (var connection = redisConnectionFactory.getConnection()) {
            connection.serverCommands().flushAll();
        }
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

        protected String registerUser(String usernamePrefix) throws Exception {
                String email = uniqueEmail();
                String username = validUsername(usernamePrefix);
                String request = """
                {
                    "username": "%s",
                    "email": "%s",
                    "password": "password123"
                }
                """.formatted(username, email);

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

        protected Cookie jwtCookieForEmail(String email) throws Exception {
            return new Cookie("jwt", loginAndGetJwt(email));
        }

        protected String createQuestionAndGetId(String email) throws Exception {
            Long productId = getAnyProductId();
            String marker = "qa-question-" + System.nanoTime();
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

            String encodedQuery = URLEncoder.encode(marker, StandardCharsets.UTF_8);
            MvcResult searchResult = mockMvc.perform(get("/api/questions/" + productId + "/search?query=" + encodedQuery)
                    .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk())
                .andReturn();

            var root = objectMapper.readTree(searchResult.getResponse().getContentAsString());
            var content = root.get("content");
            Assertions.assertTrue(content != null && content.isArray() && !content.isEmpty(),
                "Expected created question to appear in search results");
            return content.get(0).get("id").asText();
        }

        protected String createAnswerAndGetId(String email, String questionId) throws Exception {
            long qId = Long.parseLong(questionId);
            Long productId = findProductIdForQuestion(qId);
            String marker = "qa-answer-" + System.nanoTime();
            String request = """
            {
              "questionId": %d,
              "text": "%s"
            }
            """.formatted(qId, marker);

            mockMvc.perform(post("/api/answers/submit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
                    .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk());

            MvcResult listResult = mockMvc.perform(get("/api/questions/" + productId)
                    .cookie(jwtCookieForEmail(email)))
                .andExpect(status().isOk())
                .andReturn();

            var root = objectMapper.readTree(listResult.getResponse().getContentAsString());
            var content = root.get("content");
            Assertions.assertNotNull(content, "Expected paged question content");

            for (var questionNode : content) {
                if (questionNode.get("id").asLong() != qId) {
                    continue;
                }
                var answers = questionNode.get("answers");
                if (answers == null || !answers.isArray()) {
                    continue;
                }
                for (var answerNode : answers) {
                    if (marker.equals(answerNode.get("text").asText())) {
                        return answerNode.get("id").asText();
                    }
                }
            }

            throw new AssertionError("Expected created answer to appear in question answers");
        }

        protected Long findProductIdForQuestion(long questionId) throws Exception {
            MvcResult productsResult = mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andReturn();
            var products = objectMapper.readTree(productsResult.getResponse().getContentAsString());
            Assertions.assertTrue(products.isArray() && !products.isEmpty(), "Expected seeded products to exist");

            for (var productNode : products) {
                long productId = productNode.get("id").asLong();
                MvcResult questionsResult = mockMvc.perform(get("/api/questions/" + productId))
                    .andExpect(status().isOk())
                    .andReturn();

                var questionsRoot = objectMapper.readTree(questionsResult.getResponse().getContentAsString());
                var content = questionsRoot.get("content");
                if (content == null || !content.isArray()) {
                    continue;
                }
                for (var questionNode : content) {
                    if (questionNode.get("id").asLong() == questionId) {
                        return productId;
                    }
                }
            }

            throw new AssertionError("Could not locate product for question id " + questionId);
        }

                protected Long getAnyProductId() throws Exception {
                MvcResult result = mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andReturn();

                var products = objectMapper.readTree(result.getResponse().getContentAsString());
                Assertions.assertTrue(products.isArray() && !products.isEmpty(), "Expected seeded products to exist");
                return products.get(0).get("id").asLong();
                }

            protected String getAnyProductName() throws Exception {
                MvcResult result = mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andReturn();

                var products = objectMapper.readTree(result.getResponse().getContentAsString());
                Assertions.assertTrue(products.isArray() && !products.isEmpty(), "Expected seeded products to exist");
                return products.get(0).get("name").asText();
            }

            protected Long getAnyIngredientId() throws Exception {
                MvcResult result = mockMvc.perform(get("/api/ingredients"))
                    .andExpect(status().isOk())
                    .andReturn();

                var ingredientsRoot = objectMapper.readTree(result.getResponse().getContentAsString());
                var content = ingredientsRoot.get("content");
                Assertions.assertTrue(content != null && content.isArray() && !content.isEmpty(),
                    "Expected seeded ingredients to exist");
                return content.get(0).get("id").asLong();
            }

            protected void addToWishlist(String email, Long productId, String shadeName) throws Exception {
                String request = """
                {
                  "productId": %d,
                  "shadeName": %s
                }
                """.formatted(productId, shadeName == null ? "null" : "\"" + shadeName + "\"");

                mockMvc.perform(post("/api/wishlist/add")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                    .andExpect(status().isOk());
            }

            protected void addProductToBreakoutList(String email, Long productId) throws Exception {
                String request = """
                {
                  "productId": %d,
                  "ingredientId": null
                }
                """.formatted(productId);

                mockMvc.perform(post("/api/breakout-list/add")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                    .andExpect(status().isOk());
            }

            protected void addIngredientToBreakoutList(String email, Long ingredientId) throws Exception {
                String request = """
                {
                  "productId": null,
                  "ingredientId": %d
                }
                """.formatted(ingredientId);

                mockMvc.perform(post("/api/breakout-list/add")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                    .andExpect(status().isOk());
            }

            protected void removeFromBreakoutList(String email, Long productId, Long ingredientId) throws Exception {
                String request = """
                {
                  "productId": %s,
                  "ingredientId": %s
                }
                """.formatted(productId == null ? "null" : productId.toString(),
                    ingredientId == null ? "null" : ingredientId.toString());

                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/breakout-list/remove")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                    .andExpect(status().isOk());
            }

            protected Long createMakeupRoutineAndGetId(String email) throws Exception {
                String routineName = "routine-it-" + System.nanoTime();
                String request = """
                {
                  "occasion": "EVENT",
                  "name": "%s",
                  "notes": "integration test routine"
                }
                """.formatted(routineName);

                mockMvc.perform(post("/api/routines/makeup")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                    .andExpect(status().isOk());

                MvcResult routinesResult = mockMvc.perform(get("/api/routines/makeup")
                        .cookie(jwtCookieForEmail(email)))
                    .andExpect(status().isOk())
                    .andReturn();

                var routines = objectMapper.readTree(routinesResult.getResponse().getContentAsString());
                Assertions.assertTrue(routines.isArray(), "Expected routine list to be an array");
                for (var routine : routines) {
                    if (routineName.equals(routine.get("name").asText())) {
                        return routine.get("routineId").asLong();
                    }
                }

                throw new AssertionError("Expected created routine to appear in makeup routines list");
            }

            protected void addProductToRoutine(String email, Long routineId, Long productId, String shadeName) throws Exception {
                String request = """
                {
                  "productId": %d,
                  "shadeName": %s
                }
                """.formatted(productId, shadeName == null ? "null" : "\"" + shadeName + "\"");

                mockMvc.perform(post("/api/routines/" + routineId + "/add-product")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                    .andExpect(status().isOk());
            }

                protected Long createReviewAndGetId(String email) throws Exception {
                Long productId = getAnyProductId();
                String marker = "review-it-" + System.nanoTime();
                String request = """
                {
                  "productId": %d,
                  "rating": 5,
                  "title": "%s",
                  "text": "%s"
                }
                """.formatted(productId, marker, marker);

                mockMvc.perform(post("/api/reviews/add")
                        .cookie(jwtCookieForEmail(email))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                    .andExpect(status().isOk());

                String encodedQuery = URLEncoder.encode(marker, StandardCharsets.UTF_8);
                MvcResult searchResult = mockMvc.perform(get("/api/reviews/" + productId + "/search?query=" + encodedQuery)
                        .cookie(jwtCookieForEmail(email)))
                    .andExpect(status().isOk())
                    .andReturn();

                var root = objectMapper.readTree(searchResult.getResponse().getContentAsString());
                var content = root.get("content");
                Assertions.assertTrue(content != null && content.isArray() && !content.isEmpty(),
                    "Expected created review to appear in search results");

                return content.get(0).get("reviewId").asLong();
                }

            protected Long createDiscussionAndGetId(String email) throws Exception {
                String marker = "discussion-it-" + System.nanoTime();
                return createDiscussionAndGetId(email, marker, marker);
            }

            protected Long createDiscussionAndGetId(String email, String title, String text) throws Exception {
                String request = """
                {
                  "title": "%s",
                  "text": "%s"
                }
                """.formatted(title, text);

                mockMvc.perform(post("/api/discussions")
                                .cookie(jwtCookieForEmail(email))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                        .andExpect(status().isOk());

                String encodedQuery = URLEncoder.encode(title, StandardCharsets.UTF_8);
                MvcResult searchResult = mockMvc.perform(get("/api/discussions/search?query=" + encodedQuery)
                                .cookie(jwtCookieForEmail(email)))
                        .andExpect(status().isOk())
                        .andReturn();

                var root = objectMapper.readTree(searchResult.getResponse().getContentAsString());
                var content = root.get("content");
                Assertions.assertTrue(content != null && content.isArray() && !content.isEmpty(),
                        "Expected created discussion to appear in search results");

                for (var discussionNode : content) {
                    if (title.equals(discussionNode.get("title").asText())) {
                        return discussionNode.get("id").asLong();
                    }
                }

                throw new AssertionError("Expected created discussion to be present in search content");
            }

            protected Long createDiscussionCommentAndGetId(String email, long discussionId) throws Exception {
                String marker = "discussion-comment-it-" + System.nanoTime();
                return createDiscussionCommentAndGetId(email, discussionId, marker);
            }

            protected Long createDiscussionCommentAndGetId(String email, long discussionId, String text) throws Exception {
                String request = """
                {
                  "parentDiscussionCommentId": null,
                  "text": "%s"
                }
                """.formatted(text);

                mockMvc.perform(post("/api/discussions/" + discussionId + "/comment")
                                .cookie(jwtCookieForEmail(email))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                        .andExpect(status().isOk());

                MvcResult discussionsResult = mockMvc.perform(get("/api/discussions")
                                .cookie(jwtCookieForEmail(email)))
                        .andExpect(status().isOk())
                        .andReturn();

                var root = objectMapper.readTree(discussionsResult.getResponse().getContentAsString());
                var content = root.get("content");
                Assertions.assertNotNull(content, "Expected paged discussions content");

                for (var discussionNode : content) {
                    if (discussionNode.get("id").asLong() != discussionId) {
                        continue;
                    }
                    var comments = discussionNode.get("comments");
                    if (comments == null || !comments.isArray()) {
                        continue;
                    }
                    for (var commentNode : comments) {
                        if (text.equals(commentNode.get("text").asText())) {
                            return commentNode.get("id").asLong();
                        }
                    }
                }

                throw new AssertionError("Expected created comment to appear in discussion comments");
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

    private static void configureWindowsDockerHostForTestcontainers() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        if (!osName.contains("win")) {
            return;
        }
        if (!isBlank(System.getProperty("docker.host")) || !isBlank(System.getenv("DOCKER_HOST"))) {
            return;
        }
        System.setProperty("docker.host", "npipe:////./pipe/dockerDesktopLinuxEngine");
    }

    private static boolean isDockerAvailableForTestcontainers() {
        try {
            return DockerClientFactory.instance().isDockerAvailable();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static void ensureComposeDbIsReachable() {
        try (Socket ignored = new Socket(DB_HOST, DB_PORT)) {
            // Port is open and reachable.
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Integration tests require PostgreSQL at " + DB_HOST + ":" + DB_PORT
                            + ". Start it with `docker compose -f docker-compose.dev.yml up db` from the repository root.",
                    ex);
        }
    }

    private static String valueOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        if (!isBlank(value)) {
            return value;
        }
        value = System.getProperty(key);
        if (!isBlank(value)) {
            return value;
        }
        value = loadFromDotEnv(key);
        if (!isBlank(value)) {
            return value;
        }
        return defaultValue;
    }

    private static void ensureIsolatedTestDatabase(String dbUser, String dbPassword, String dbName) {
        String adminUrl = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/postgres";
        try (Connection connection = DriverManager.getConnection(adminUrl, dbUser, dbPassword);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE \"" + dbName + "\"");
        } catch (SQLException ex) {
            if (!"42P04".equals(ex.getSQLState())) {
                throw new IllegalStateException("Unable to create isolated integration-test database '" + dbName + "'.", ex);
            }
        }
    }

    private static String validUsername(String usernamePrefix) {
        String base = usernamePrefix == null ? "user" : usernamePrefix;
        base = base.replaceAll("[^a-zA-Z0-9_]", "_");
        if (base.isBlank()) {
            base = "user";
        }

        String suffix = Long.toUnsignedString(System.nanoTime(), 36);
        int maxPrefixLength = Math.max(1, 30 - suffix.length() - 1);
        if (base.length() > maxPrefixLength) {
            base = base.substring(0, maxPrefixLength);
        }
        return base + "_" + suffix;
    }
}
