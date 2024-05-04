package com.project.ensureQuality.repository;

import com.project.ensureQuality.model.User;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
public class AuthRepositoryTest {
    @Mock
    private UserRepository userRepository;
    public AuthRepositoryTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findByUsername() {
        String username = "username";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Optional<User> result = userRepository.findByUsername(username);

        assertEquals(Optional.of(user), result);
    }

    @Test
    public void findByEmail() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> result = userRepository.findByEmail(email);

        assertEquals(Optional.of(user), result);
    }

    @Test
    public void existsByPhoneNumber() {
        String phoneNumber = "0987654321";

        when(userRepository.existsByPhoneNumber(phoneNumber)).thenReturn(true);

        boolean result = userRepository.existsByPhoneNumber(phoneNumber);

        assertEquals(true, result);
    }

    @Test
    public void existsByEmail() {
        String email = "email@gmail.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean result = userRepository.existsByEmail(email);

        assertEquals(true, result);
    }
    @Test
    public void getUserWithPagination() {
        int offset = 0;
        int limit = 10;
        User user1 = new User();
        User user2 = new User();
        List<User> expectedUsers = Arrays.asList(user1, user2);

        when(userRepository.getUserWithPagination(offset, limit)).thenReturn(expectedUsers);

        List<User> result = userRepository.getUserWithPagination(offset, limit);

        assertEquals(expectedUsers, result);
    }

    @Test
    public void findByEmailOrPhoneNumber() {
        String valueLogin = "test@example.com";
        User user = new User();
        user.setEmail(valueLogin);

        when(userRepository.findByEmailOrPhoneNumber(valueLogin)).thenReturn(Optional.of(user));

        Optional<User> result = userRepository.findByEmailOrPhoneNumber(valueLogin);

        assertEquals(Optional.of(user), result);
    }
}
