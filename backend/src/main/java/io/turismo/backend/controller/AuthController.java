package io.turismo.backend.controller;

import io.turismo.backend.dto.user.UserDTO;
import io.turismo.backend.dto.user.UserLoginDTO;
import io.turismo.backend.model.RefreshToken;
import io.turismo.backend.model.User;
import io.turismo.backend.repository.RefreshTokenRepository;
import io.turismo.backend.repository.UserRepository;
import io.turismo.backend.security.CookieUtils;
import io.turismo.backend.service.RefreshTokenService;
import io.turismo.backend.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.turismo.backend.config.SecurityConfig;

@RestController
@SecurityRequirement(name = SecurityConfig.SECURITY)
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieUtils cookieUtils;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.email())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "E-mail ou senha incorretos."));
        }

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

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(name = CookieUtils.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshTokenStr) {

        if (refreshTokenStr == null || refreshTokenStr.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh Token ausente. Faça login novamente."));
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .map(refreshTokenService::verifyExpiration)
                .orElse(null);

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh Token inválido ou revogado."));
        }

        User user = refreshToken.getUser();

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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = CookieUtils.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshTokenStr) {

        if (refreshTokenStr != null && !refreshTokenStr.isBlank()) {
            refreshTokenService.deleteByToken(refreshTokenStr);
        }

        ResponseCookie cleanCookie = cookieUtils.cleanRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(Map.of("message", "Logout realizado com sucesso."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Usuário não autenticado."));
        }
        UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole(), user.getSpotManager() != null ? user.getSpotManager().getSpotManagerId() : null);
        return ResponseEntity.ok(userDTO);
    }
}
