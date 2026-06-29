package io.turismo.backend.dto.state;

import io.turismo.backend.model.enums.StateName;
import jakarta.validation.constraints.NotNull;

public record StateCreateDTO(
        @NotNull StateName name
) {}
