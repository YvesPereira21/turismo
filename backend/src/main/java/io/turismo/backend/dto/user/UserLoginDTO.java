package io.turismo.backend.dto.user;

public record UserLoginDTO(
    String email,
    String password
) {}
