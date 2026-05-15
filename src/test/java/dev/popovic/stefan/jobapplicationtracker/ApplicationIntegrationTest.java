package dev.popovic.stefan.jobapplicationtracker;

import dev.popovic.stefan.jobapplicationtracker.dto.application.ApplicationResponse;
import dev.popovic.stefan.jobapplicationtracker.dto.application.CreateApplicationRequest;
import dev.popovic.stefan.jobapplicationtracker.dto.auth.AuthResponse;
import dev.popovic.stefan.jobapplicationtracker.dto.auth.LoginRequest;
import dev.popovic.stefan.jobapplicationtracker.dto.auth.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import dev.popovic.stefan.jobapplicationtracker.entity.ApplicationStatus;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Testcontainers
class ApplicationIntegrationTest {

    @Container
    @SuppressWarnings({"rawtypes", "resource"})
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void fullApplicationLifecycle() {
        String email = "test-" + UUID.randomUUID() + "@example.com";
        String password = "password123";

        // 1. Register
        RegisterRequest registerRequest = new RegisterRequest(email, password);
        ResponseEntity<AuthResponse> registerResponse = restTemplate.postForEntity(
                "/auth/register", registerRequest, AuthResponse.class);
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // 2. Login
        LoginRequest loginRequest = new LoginRequest(email, password);
        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
                "/auth/login", loginRequest, AuthResponse.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token = loginResponse.getBody().token();

        // 3. Auth headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        // 4. Create application
        CreateApplicationRequest createRequest = new CreateApplicationRequest(
                "Acme Corp", "Software Engineer", ApplicationStatus.APPLIED, null, null, null);
        ResponseEntity<ApplicationResponse> createResponse = restTemplate.exchange(
                "/applications", HttpMethod.POST,
                new HttpEntity<>(createRequest, headers), ApplicationResponse.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ApplicationResponse created = createResponse.getBody();
        assertThat(created.company()).isEqualTo("Acme Corp");
        assertThat(created.role()).isEqualTo("Software Engineer");

        // 5. Fetch all — expect exactly the one application we just created
        ResponseEntity<List<ApplicationResponse>> listResponse = restTemplate.exchange(
                "/applications", HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<ApplicationResponse>>() {});
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).hasSize(1);

        // 6. Fetch by ID
        ResponseEntity<ApplicationResponse> getByIdResponse = restTemplate.exchange(
                "/applications/" + created.id(), HttpMethod.GET,
                new HttpEntity<>(headers), ApplicationResponse.class);
        assertThat(getByIdResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApplicationResponse fetched = getByIdResponse.getBody();
        assertThat(fetched.id()).isEqualTo(created.id());
        assertThat(fetched.company()).isEqualTo("Acme Corp");
        assertThat(fetched.role()).isEqualTo("Software Engineer");
    }
}
