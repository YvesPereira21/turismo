package io.turismo.backend.service;

import io.turismo.backend.model.RefreshToken;
import io.turismo.backend.model.User;
import io.turismo.backend.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        user = new User();
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());

        refreshToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateRefreshToken() {
        when(refreshTokenRepository.deleteByUser(user)).thenReturn(1);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken result = refreshTokenService.createRefreshToken(user);

        assertNotNull(result);
        assertEquals(user, result.getUser());

        verify(refreshTokenRepository, times(1)).deleteByUser(user);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void shouldVerifyExpirationAndReturnTokenWhenNotExpired() {
        RefreshToken result = refreshTokenService.verifyExpiration(refreshToken);

        assertNotNull(result);
        assertEquals(refreshToken, result);

        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    void shouldDeleteByToken() {
        String tokenStr = refreshToken.getToken();
        when(refreshTokenRepository.deleteByToken(tokenStr)).thenReturn(1);

        assertDoesNotThrow(() -> refreshTokenService.deleteByToken(tokenStr));

        verify(refreshTokenRepository, times(1)).deleteByToken(tokenStr);
    }

    @Test
    void shouldDeleteByUser() {
        when(refreshTokenRepository.deleteByUser(user)).thenReturn(1);

        assertDoesNotThrow(() -> refreshTokenService.deleteByUser(user));

        verify(refreshTokenRepository, times(1)).deleteByUser(user);
    }

    @Test
    void shouldGetRefreshTokenDurationSeconds() {
        long duration = refreshTokenService.getRefreshTokenDurationSeconds();

        assertEquals(604800L, duration);
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotVerifyExpirationAndThrowExceptionWhenExpired() {
        RefreshToken expiredToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().minusSeconds(3600))
                .build();

        doNothing().when(refreshTokenRepository).delete(expiredToken);

        assertThrows(
                RuntimeException.class,
                () -> refreshTokenService.verifyExpiration(expiredToken)
        );

        verify(refreshTokenRepository, times(1)).delete(expiredToken);
    }
}
