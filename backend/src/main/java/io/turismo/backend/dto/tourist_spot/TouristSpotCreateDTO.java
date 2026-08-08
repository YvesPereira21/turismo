package io.turismo.backend.dto.tourist_spot;

import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.turismo.backend.dto.social_media.SocialMediaCreateDTO;
import java.util.List;

public record TouristSpotCreateDTO(
        @NotBlank String name,
        @NotNull Double latitude,
        @NotNull Double longitude,
        @NotNull LocalTime opensAt,
        @NotNull LocalTime closesAt,
        @NotBlank String shortDescription,
        @NotBlank String description,
        @NotNull UUID cityId,
        @Size(min = 1, max = 7) Set<String> tags,
        List<SocialMediaCreateDTO> socialsMedia
) {}
