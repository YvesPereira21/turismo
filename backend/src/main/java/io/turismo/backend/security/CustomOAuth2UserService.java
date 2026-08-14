package io.turismo.backend.security;

import io.turismo.backend.model.User;
import io.turismo.backend.model.enums.AuthProvider;
import io.turismo.backend.model.enums.UserRole;
import io.turismo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub");
        if (providerId == null) {
            providerId = oAuth2User.getAttribute("id");
        }

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

        if (providerId != null) {
            final String finalProviderId = providerId;
            userRepository.findByProviderAndProviderId(provider, finalProviderId)
                    .or(() -> {
                        if (email != null) {
                            return userRepository.findByEmail(email).map(existingUser -> {
                                existingUser.setProvider(provider);
                                existingUser.setProviderId(finalProviderId);
                                return userRepository.save(existingUser);
                            });
                        }
                        return Optional.empty();
                    })
                    .orElseGet(() -> {
                        User newUser = User.builder()
                                .name(name != null ? name : "Usuário Google")
                                .email(email)
                                .password(new BCryptPasswordEncoder().encode(UUID.randomUUID().toString()))
                                .role(UserRole.TOURIST)
                                .provider(provider)
                                .providerId(finalProviderId)
                                .build();
                        return userRepository.save(newUser);
                    });
        }

        return oAuth2User;
    }
}
