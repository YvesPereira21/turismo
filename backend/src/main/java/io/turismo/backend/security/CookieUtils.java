package io.turismo.backend.security;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    public ResponseCookie createRefreshTokenCookie(String token, long maxAgeSeconds) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(false) // true se estiver em ambiente de produção com HTTPS
                .path("/api/v1/auth")
                .maxAge(maxAgeSeconds)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie cleanRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .path("/api/v1/auth")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }
}
