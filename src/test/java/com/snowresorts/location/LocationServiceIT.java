package com.snowresorts.location;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Integration test for location-service. Spins up real Postgres (PostGIS) and Redis containers
 * so the full Spring context (Flyway, JPA, Redis listener container, STOMP broker, security)
 * boots. Asserts the group endpoint is authenticated.
 *
 * <p>Compile-only here: the Docker daemon is not available in this environment, so the suite is
 * not executed (run with {@code -DskipITs} disabled in CI where Docker is present).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class LocationServiceIT {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse("imresamu/postgis:16-3.4").asCompatibleSubstituteFor("postgres"))
            .withDatabaseName("snow_resorts")
            .withUsername("snow")
            .withPassword("snow");

    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>(
            DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /groups without a token returns 401")
    void createGroup_withoutAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/snow-resort-service/v1/location/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Powder Hounds"}"""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /groups with a valid JWT creates the group and auto-joins the creator")
    void createGroup_withJwt_returnsCreated() throws Exception {
        String userId = UUID.randomUUID().toString();
        mockMvc.perform(post("/snow-resort-service/v1/location/groups")
                        .with(jwt().jwt(jwt -> jwt.subject(userId).claim("roles", List.of("USER"))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Powder Hounds"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.inviteCode").exists())
                .andExpect(jsonPath("$.createdBy").value(userId))
                .andExpect(jsonPath("$.members[0].userId").value(userId));
    }
}
