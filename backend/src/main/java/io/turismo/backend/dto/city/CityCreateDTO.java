package io.turismo.backend.dto.city;

import jakarta.validation.constraints.NotBlank;

public record CityCreateDTO(
        @NotBlank String name,
        @NotBlank String stateName
) {}
