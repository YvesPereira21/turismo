package io.turismo.backend.dto.user;

import io.turismo.backend.model.enums.UserRole;
import java.util.UUID;

public record UserDTO(
    UUID id,
    String name,
    String email,
    String phone,
    UserRole role
) {}