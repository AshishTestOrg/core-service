package com.ashishtestorg.coreservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ashishtestorg.coreservice.dto.UserRequestDTO;
import com.ashishtestorg.coreservice.dto.UserResponseDTO;
import com.ashishtestorg.coreservice.exception.GlobalExceptionHandler;
import com.ashishtestorg.coreservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUserShouldReturnCreated() throws Exception {
        UserRequestDTO requestDTO = request();
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(response());

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ashish@example.com"));
    }

    @Test
    void getAllUsersShouldReturnPage() throws Exception {
        when(userService.getAllUsers(PageRequest.of(0, 10, org.springframework.data.domain.Sort.by("id").ascending())))
                .thenReturn(new PageImpl<>(java.util.List.of(response()), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void getUserByIdShouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(response());

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ashish"));
    }

    @Test
    void updateUserShouldReturnOk() throws Exception {
        when(userService.updateUser(eq(1L), any(UserRequestDTO.class))).thenReturn(response());

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Test"));
    }

    @Test
    void deleteUserShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchByEmailShouldReturnUser() throws Exception {
        when(userService.getUserByEmail("ashish@example.com")).thenReturn(response());

        mockMvc.perform(get("/api/v1/users/search").param("email", "ashish@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createUserShouldReturnValidationErrorsForBadPayload() throws Exception {
        UserRequestDTO invalidRequest = UserRequestDTO.builder()
                .firstName("")
                .lastName("")
                .email("bad-email")
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.firstName").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists());
    }

    private UserRequestDTO request() {
        return UserRequestDTO.builder()
                .firstName("Ashish")
                .lastName("Test")
                .email("ashish@example.com")
                .phoneNumber("1234567890")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Test Address")
                .build();
    }

    private UserResponseDTO response() {
        return UserResponseDTO.builder()
                .id(1L)
                .firstName("Ashish")
                .lastName("Test")
                .email("ashish@example.com")
                .phoneNumber("1234567890")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Test Address")
                .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .build();
    }
}
