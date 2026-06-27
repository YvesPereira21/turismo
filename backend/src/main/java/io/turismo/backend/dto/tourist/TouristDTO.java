package io.turismo.backend.dto.tourist;


import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record TouristDTO(
    UUID touristId,
    LocalDate birthDate,
    UUID userId,
    String name,
    String phone
) {}
