package io.turismo.backend.dto.tourist_spot;

import jakarta.validation.constraints.Size;
import java.time.LocalTime;

import java.util.Set;
import java.util.UUID;

public record TouristSpotUpdateDTO(
    String name,
    Double latitude,
    Double longitude,
    LocalTime opensAt,
    LocalTime closesAt,
    String shortDescription,
    String description,
    UUID cityId,
    @Size(min = 1, max = 7) Set<String> tags
) {}
