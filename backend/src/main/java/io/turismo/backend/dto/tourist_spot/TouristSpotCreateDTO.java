package io.turismo.backend.dto.tourist_spot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record TouristSpotCreateDTO(
    @NotBlank String name,
    @NotNull Double latitude,
    @NotNull Double longitude,
    @NotBlank LocalTime opensAt,
    @NotBlank LocalTime closesAt,
    @NotBlank String shortDescription,
    @NotBlank String description,
    @NotNull UUID spotManagerId,
    @NotBlank String cityName,
    @Size(min = 1, max = 7) List<String> tags
) {}
