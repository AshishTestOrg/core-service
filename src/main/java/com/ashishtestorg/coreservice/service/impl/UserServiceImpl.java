package com.ashishtestorg.coreservice.service.impl;

import com.ashishtestorg.coreservice.dto.UserRequestDTO;
import com.ashishtestorg.coreservice.dto.UserResponseDTO;
import com.ashishtestorg.coreservice.entity.User;
import com.ashishtestorg.coreservice.exception.DuplicateEmailException;
import com.ashishtestorg.coreservice.exception.UserNotFoundException;
import com.ashishtestorg.coreservice.repository.UserRepository;
import com.ashishtestorg.coreservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        String email = userRequestDTO.getEmail().trim();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException("User already exists with email: " + email);
        }

        User savedUser = userRepository.save(toEntity(userRequestDTO));
        log.info("event=user_created id={} email={}", savedUser.getId(), savedUser.getEmail());
        return toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        log.info("event=user_list_requested page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        return toResponse(findUser(id));
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        User user = findUser(id);
        String email = userRequestDTO.getEmail().trim();
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateEmailException("User already exists with email: " + email);
        }

        user.setFirstName(userRequestDTO.getFirstName().trim());
        user.setLastName(userRequestDTO.getLastName().trim());
        user.setEmail(email);
        user.setPhoneNumber(userRequestDTO.getPhoneNumber());
        user.setDateOfBirth(userRequestDTO.getDateOfBirth());
        user.setAddress(userRequestDTO.getAddress());

        User updatedUser = userRepository.save(user);
        log.info("event=user_updated id={} email={}", updatedUser.getId(), updatedUser.getEmail());
        return toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = findUser(id);
        userRepository.delete(user);
        log.info("event=user_deleted id={} email={}", user.getId(), user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email.trim())
                .map(this::toResponse)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    private User toEntity(UserRequestDTO userRequestDTO) {
        return User.builder()
                .firstName(userRequestDTO.getFirstName().trim())
                .lastName(userRequestDTO.getLastName().trim())
                .email(userRequestDTO.getEmail().trim())
                .phoneNumber(userRequestDTO.getPhoneNumber())
                .dateOfBirth(userRequestDTO.getDateOfBirth())
                .address(userRequestDTO.getAddress())
                .build();
    }

    private UserResponseDTO toResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
