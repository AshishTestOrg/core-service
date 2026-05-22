package com.ashishtestorg.coreservice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ashishtestorg.coreservice.entity.User;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmailIgnoringCase() {
        userRepository.save(User.builder()
                .firstName("Ashish")
                .lastName("Test")
                .email("ashish@example.com")
                .phoneNumber("1234567890")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Address")
                .build());

        var result = userRepository.findByEmailIgnoreCase("ASHISH@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Ashish");
    }

    @Test
    void shouldDetectDuplicateEmailForDifferentId() {
        User saved = userRepository.save(User.builder()
                .firstName("Ashish")
                .lastName("Test")
                .email("ashish@example.com")
                .build());

        boolean exists = userRepository.existsByEmailIgnoreCaseAndIdNot("ASHISH@example.com", saved.getId() + 1);

        assertThat(exists).isTrue();
    }
}
