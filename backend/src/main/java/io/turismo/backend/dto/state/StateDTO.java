package io.turismo.backend.dto.state;

import java.util.UUID;

public record StateDTO(
        UUID stateId,
        String name
) {}
