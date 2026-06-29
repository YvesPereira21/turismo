package io.turismo.backend.dto.tourist_spot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record TouristSpotCreateDTO(
    @NotBlank String name,
    @NotNull Double latitude,
    @NotNull Double longitude,
    @NotBlank LocalTime opensAt,
    @NotBlank LocalTime closesAt,
    @NotBlank String shortDescription,
    @NotBlank String description,
    @NotBlank UUID cityId,
    @Size(min = 1, max = 7) Set<String> tags
) {}
