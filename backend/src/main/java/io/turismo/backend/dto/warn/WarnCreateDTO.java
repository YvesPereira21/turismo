package io.turismo.backend.dto.warn;

import jakarta.validation.constraints.NotBlank;

public record WarnCreateDTO(
        @NotBlank String name,
        @NotBlank String description
) {}
