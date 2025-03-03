package com.example.lab4.service;

import com.example.lab4.model.User;
import com.example.lab4.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testCreateUser() {
        User user = new User(null, "Alice", "alice@example.com");
        User savedUser = new User(1L, "Alice", "alice@example.com");
        when(userRepository.save(user)).thenReturn(savedUser);

        User result = userService.createUser(user);
        assertEquals(1L, result.getId());
        assertEquals("Alice", result.getName());
    }
}