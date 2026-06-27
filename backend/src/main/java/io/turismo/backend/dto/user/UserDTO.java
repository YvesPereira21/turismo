package io.turismo.backend.dto.user;

import java.util.UUID;

public record UserDTO(
    UUID id,
    String name,
    String email,
    String phone
) {}