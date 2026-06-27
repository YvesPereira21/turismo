package io.turismo.backend.dto.tag;

import jakarta.validation.constraints.NotBlank;

public record TagCreateUpdateDTO(
        @NotBlank String name
) {}
