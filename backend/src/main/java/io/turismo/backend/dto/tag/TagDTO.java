package io.turismo.backend.dto.tag;

import java.util.UUID;

public record TagDTO(
    UUID id,
    String name
) {}
