package io.turismo.backend.service;

import io.turismo.backend.exception.ObjectAlreadyExistsException;
import io.turismo.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private String email;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldVerifyUserAlreadyExistsAndNotThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.existsByEmail(email)).thenReturn(false);

        assertDoesNotThrow(() -> userService.verifyUserAlreadyExists(email));

        verify(userRepository, times(1)).existsByEmail(email);
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotVerifyUserAlreadyExistsAndThrowExceptionWhenUserExists() {
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(
                ObjectAlreadyExistsException.class,
                () -> userService.verifyUserAlreadyExists(email)
        );

        verify(userRepository, times(1)).existsByEmail(email);
    }
}
