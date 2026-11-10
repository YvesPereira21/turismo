package io.turismo.backend.controller;

import io.turismo.backend.controller.doc.AuthDoc;
import io.turismo.backend.dto.ApiError;
import io.turismo.backend.dto.user.UserDTO;
import io.turismo.backend.dto.user.UserLoginDTO;
import io.turismo.backend.model.RefreshToken;
import io.turismo.backend.model.User;
import io.turismo.backend.ratelimit.LoginAttemptLimiter;
import io.turismo.backend.repository.RefreshTokenRepository;
import io.turismo.backend.repository.UserRepository;
import io.turismo.backend.security.CookieUtils;
import io.turismo.backend.ratelimit.RateLimitResponses;
import io.turismo.backend.security.TokenService;
import io.turismo.backend.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dao.DataAccessException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthDoc {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieUtils cookieUtils;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<LoginAttemptLimiter> loginAttemptLimiter;

    @Override
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginDTO, HttpServletRequest request) {
        var limiter = loginAttemptLimiter.getIfAvailable();
        LoginAttemptLimiter.Reservation reservation = null;
        if (limiter != null) {
            try {
                reservation = limiter.reserve(loginDTO.email(), request.getRemoteAddr());
            } catch (DataAccessException | IllegalStateException exception) {
                return loginLimitUnavailable(exception);
            }

            if (reservation.waitMillis() > 0) {
                String seconds = Long.toString(Math.ceilDiv(reservation.waitMillis(), 1000));
                var status = HttpStatus.TOO_MANY_REQUESTS;
                return ResponseEntity.status(status)
                        .cacheControl(CacheControl.noStore())
                        .header(HttpHeaders.RETRY_AFTER, seconds)
                        .header(RateLimitResponses.RETRY_HEADER, seconds)
                        .body(loginError(status, "Limite de requisições excedido. Tente novamente em " + seconds + " segundos."));
            }
        }

        log.info("Authentication attempt for email: {}", loginDTO.email());
        User user = userRepository.findByEmail(loginDTO.email())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            log.warn("Authentication failed for email: {}", loginDTO.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "E-mail ou senha incorretos."));
        }

        if (reservation != null) {
            try {
                limiter.completeSuccessfulLogin(reservation);
            } catch (DataAccessException exception) {
                return loginLimitUnavailable(exception);
            }
        }

        log.info("User authenticated successfully: {}", user.getEmail());

        String accessToken = tokenService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        ResponseCookie cookie = cookieUtils.createRefreshTokenCookie(refreshToken.getToken(), refreshTokenService.getRefreshTokenDurationSeconds());

        UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole(), user.getSpotManager() != null ? user.getSpotManager().getSpotManagerId() : null);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of(
                        "accessToken", accessToken,
                        "user", userDTO
                ));
    }

    private ResponseEntity<ApiError> loginLimitUnavailable(RuntimeException exception) {
        log.warn("Não foi possível atualizar a cota de login", exception);
        var status = HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status)
                .cacheControl(CacheControl.noStore())
                .body(loginError(status, "Serviço temporariamente indisponível."));
    }

    private ApiError loginError(HttpStatus status, String message) {
        return new ApiError(LocalDateTime.now(), status.value(), status.name(), List.of(message));
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(name = CookieUtils.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshTokenStr) {
        log.info("Token refresh request received");

        if (refreshTokenStr == null || refreshTokenStr.isBlank()) {
            log.warn("Token refresh rejected: missing cookie");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh Token ausente. Faça login novamente."));
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .map(refreshTokenService::verifyExpiration)
                .orElse(null);

        if (refreshToken == null) {
            log.warn("Token refresh rejected: token invalid or expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh Token inválido ou revogado."));
        }

        User user = refreshToken.getUser();
        log.info("Token refreshed successfully for user: {}", user.getEmail());

        // Rotação: Apaga o Refresh Token antigo
        refreshTokenService.deleteByToken(refreshTokenStr);

        // Gera novo Refresh Token e novo Access Token
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        String newAccessToken = tokenService.generateToken(user);
        ResponseCookie cookie = cookieUtils.createRefreshTokenCookie(newRefreshToken.getToken(), refreshTokenService.getRefreshTokenDurationSeconds());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("accessToken", newAccessToken));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = CookieUtils.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshTokenStr) {
        log.info("Logout requested");

        if (refreshTokenStr != null && !refreshTokenStr.isBlank()) {
            refreshTokenService.deleteByToken(refreshTokenStr);
        }

        ResponseCookie cleanCookie = cookieUtils.cleanRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(Map.of("message", "Logout realizado com sucesso."));
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Usuário não autenticado."));
        }
        UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole(), user.getSpotManager() != null ? user.getSpotManager().getSpotManagerId() : null);
        return ResponseEntity.ok(userDTO);
    }
}
