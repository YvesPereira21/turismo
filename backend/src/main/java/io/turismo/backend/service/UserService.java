package io.turismo.backend.service;

import io.turismo.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService{
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    protected void verifyUserAlreadyExists(String email) {
        boolean userAlreadyExists = userRepository.existsByEmail(email);
        if (userAlreadyExists) {
            throw new RuntimeException("Email ou senha inválidos");
        }
    }
}