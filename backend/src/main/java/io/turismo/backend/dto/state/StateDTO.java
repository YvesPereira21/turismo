package io.turismo.backend.dto.state;

import io.turismo.backend.model.enums.StateName;

import java.util.UUID;

public record StateDTO(
        UUID stateId,
        StateName name
) {}
