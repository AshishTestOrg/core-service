package com.ashishtestorg.coreservice.service;

import com.ashishtestorg.coreservice.dto.UserRequestDTO;
import com.ashishtestorg.coreservice.dto.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO userRequestDTO);

    Page<UserResponseDTO> getAllUsers(Pageable pageable);

    UserResponseDTO getUserById(Long id);

    UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO);

    void deleteUser(Long id);

    UserResponseDTO getUserByEmail(String email);
}
