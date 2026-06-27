package io.turismo.backend.dto.warn;

import java.time.LocalDate;
import java.util.UUID;

public record WarnDTO(
    UUID id,
    String name,
    String description,
    LocalDate eventDate
) {}
