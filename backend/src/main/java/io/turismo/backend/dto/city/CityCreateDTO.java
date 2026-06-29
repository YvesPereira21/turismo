package io.turismo.backend.dto.city;

import io.turismo.backend.model.enums.StateName;
import jakarta.validation.constraints.NotBlank;

public record CityCreateDTO(
        @NotBlank String name,
        @NotBlank StateName stateName
) {}
