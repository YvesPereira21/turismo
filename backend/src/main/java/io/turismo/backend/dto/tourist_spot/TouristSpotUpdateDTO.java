package io.turismo.backend.dto.tourist_spot;

import jakarta.validation.constraints.Size;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record TouristSpotUpdateDTO(
    String name,
    Double latitude,
    Double longitude,
    LocalTime opensAt,
    LocalTime closesAt,
    String shortDescription,
    String description,
    UUID spotManagerId,
    String cityName,
    @Size(min = 1, max = 7) List<String> tags
) {}
