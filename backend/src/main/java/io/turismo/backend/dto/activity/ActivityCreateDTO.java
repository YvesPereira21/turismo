package io.turismo.backend.dto.activity;

import jakarta.validation.constraints.NotBlank;

public record ActivityCreateDTO(
        @NotBlank String name
) {}
