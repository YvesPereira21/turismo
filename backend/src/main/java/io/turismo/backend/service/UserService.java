package io.turismo.backend.service;

import io.turismo.backend.exception.ObjectAlreadyExistsException;
import io.turismo.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService{
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    protected void verifyUserAlreadyExists(String email) {
        boolean userAlreadyExists = userRepository.existsByEmail(email);
        if (userAlreadyExists) {
            log.warn("Registration failed: email {} already exists", email);
            throw new ObjectAlreadyExistsException("Email ou senha inválidos");
        }
    }
}