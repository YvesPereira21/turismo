package io.turismo.backend.security;

import io.turismo.backend.model.enums.AuthProvider;
import io.turismo.backend.model.RefreshToken;
import io.turismo.backend.model.User;
import io.turismo.backend.repository.UserRepository;
import io.turismo.backend.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final CookieUtils cookieUtils;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String providerId = oAuth2User.getAttribute("sub");
        if (providerId == null) {
            providerId = oAuth2User.getAttribute("id");
        }

        User user = null;
        if (providerId != null) {
            user = userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, providerId).orElse(null);
        }
        if (user == null && email != null) {
            user = userRepository.findByEmail(email).orElse(null);
        }
        if (user == null) {
            log.warn("OAuth2 login failed: user not found in database for email: {}", email);
            throw new RuntimeException("Usuário autenticado no Google não encontrado na base de dados");
        }

        log.info("OAuth2 authentication successful for user: {}", user.getEmail());

        String token = tokenService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        ResponseCookie cookie = cookieUtils.createRefreshTokenCookie(refreshToken.getToken(), refreshTokenService.getRefreshTokenDurationSeconds());

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:4200/auth/callback")
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
