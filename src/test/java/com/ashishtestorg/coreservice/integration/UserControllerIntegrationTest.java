package com.ashishtestorg.coreservice.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPerformFullCrudFlowAndExposeHealthAndSwagger() throws Exception {
        String createRequest = """
                {
                  "firstName": "Ashish",
                  "lastName": "Test",
                  "email": "ashish@example.com",
                  "phoneNumber": "1234567890",
                  "dateOfBirth": "1990-01-01",
                  "address": "Test Address"
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("ashish@example.com"))
                .andReturn();

        long userId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(userId));

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ashish"));

        mockMvc.perform(get("/api/v1/users/search").param("email", "ashish@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));

        String updateRequest = """
                {
                  "firstName": "Ashish",
                  "lastName": "Updated",
                  "email": "ashish.updated@example.com",
                  "phoneNumber": "9999999999",
                  "dateOfBirth": "1990-01-01",
                  "address": "Updated Address"
                }
                """;

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Updated"))
                .andExpect(jsonPath("$.email").value("ashish.updated@example.com"));

        mockMvc.perform(delete("/api/v1/users/{id}", userId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: " + userId));

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));

        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }
}
