package com.example.financetracker;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:finance-db;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PersonalFinanceTrackerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"demo@example.com\",\"password\":\"DemoPass1\"}"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        jwtToken = node.get("accessToken").asText();
    }

    @Test
    void shouldReturnDashboardSummary() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary")
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.month").exists())
            .andExpect(jsonPath("$.totalIncome").exists())
            .andExpect(jsonPath("$.budgets").isArray());
    }

    @Test
    void shouldCreateTransaction() throws Exception {
        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "description": "Freelance project",
                      "amount": 1200.00,
                      "transactionDate": "2026-03-10",
                      "type": "INCOME",
                      "category": "FREELANCE",
                      "notes": "March client invoice"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.description").value("Freelance project"))
            .andExpect(jsonPath("$.type").value("INCOME"));
    }

    @Test
    void shouldRejectProtectedEndpointWithoutJwt() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRegisterWithEmailPasswordAndDisplayName() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "alice@example.com",
                      "password": "SecurePass1",
                      "displayName": "Alice"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(jsonPath("$.email").value("alice@example.com"))
            .andExpect(jsonPath("$.displayName").value("Alice"));
    }

    @Test
    void shouldRejectWeakPasswordDuringRegistration() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "weak@example.com",
                      "password": "weak",
                      "displayName": "Weak Password User"
                    }
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectDuplicateEmailDuringRegistration() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "demo@example.com",
                      "password": "AnotherPass1",
                      "displayName": "Demo Again"
                    }
                    """))
            .andExpect(status().isConflict());
    }

    @Test
    void shouldRefreshToken() throws Exception {
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "demo@example.com",
                      "password": "DemoPass1"
                    }
                    """))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String refreshToken = objectMapper.readTree(loginResponse).get("refreshToken").asText();

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "refreshToken": "%s"
                    }
                    """.formatted(refreshToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void shouldResetPasswordUsingForgotPasswordFlow() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "reset-user@example.com",
                      "password": "OriginalPass1",
                      "displayName": "Reset User"
                    }
                    """))
            .andExpect(status().isCreated());

        String forgotPasswordResponse = mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "reset-user@example.com"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resetToken").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String resetToken = objectMapper.readTree(forgotPasswordResponse).get("resetToken").asText();

        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "token": "%s",
                      "newPassword": "UpdatedPass1"
                    }
                    """.formatted(resetToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Password has been reset successfully"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "reset-user@example.com",
                      "password": "UpdatedPass1"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void shouldReturnCurrentAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("demo@example.com"))
            .andExpect(jsonPath("$.displayName").value("Demo User"));
    }
}
