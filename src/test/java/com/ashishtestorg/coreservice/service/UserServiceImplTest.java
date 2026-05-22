package com.ashishtestorg.coreservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ashishtestorg.coreservice.dto.UserRequestDTO;
import com.ashishtestorg.coreservice.dto.UserResponseDTO;
import com.ashishtestorg.coreservice.entity.User;
import com.ashishtestorg.coreservice.exception.DuplicateEmailException;
import com.ashishtestorg.coreservice.exception.UserNotFoundException;
import com.ashishtestorg.coreservice.repository.UserRepository;
import com.ashishtestorg.coreservice.service.impl.UserServiceImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDTO requestDTO;
    private User user;

    @BeforeEach
    void setUp() {
        requestDTO = UserRequestDTO.builder()
                .firstName("Ashish")
                .lastName("Test")
                .email("ashish@example.com")
                .phoneNumber("1234567890")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Test Address")
                .build();

        user = User.builder()
                .id(1L)
                .firstName("Ashish")
                .lastName("Test")
                .email("ashish@example.com")
                .phoneNumber("1234567890")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Test Address")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createUserShouldPersistUserWhenEmailIsUnique() {
        when(userRepository.existsByEmailIgnoreCase("ashish@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.createUser(requestDTO);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("ashish@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserShouldRejectDuplicateEmail() {
        when(userRepository.existsByEmailIgnoreCase("ashish@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(requestDTO))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("ashish@example.com");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUsersShouldReturnPagedResponses() {
        when(userRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.List.of(user)));

        var result = userService.getAllUsers(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Ashish");
    }

    @Test
    void updateUserShouldRejectDuplicateEmailForOtherRecord() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("ashish@example.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1L, requestDTO))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void deleteUserShouldRemoveExistingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void getUserByEmailShouldThrowWhenMissing() {
        when(userRepository.findByEmailIgnoreCase("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail("missing@example.com"))
                .isInstanceOf(UserNotFoundException.class);
    }
}
