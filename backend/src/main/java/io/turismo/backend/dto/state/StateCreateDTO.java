package io.turismo.backend.dto.state;

import jakarta.validation.constraints.NotBlank;

public record StateCreateDTO(
        @NotBlank String name
) {}
