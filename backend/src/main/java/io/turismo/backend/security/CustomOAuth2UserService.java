package io.turismo.backend.security;

import io.turismo.backend.model.User;
import io.turismo.backend.model.enums.UserRole;
import io.turismo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

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

        if (email != null) {
            userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = User.builder()
                                .name(name != null ? name : "Usuário Google")
                                .email(email)
                                .password(new BCryptPasswordEncoder().encode(UUID.randomUUID().toString()))
                                .role(UserRole.TOURIST)
                                .build();
                        return userRepository.save(newUser);
                    });
        }

        return oAuth2User;
    }
}
