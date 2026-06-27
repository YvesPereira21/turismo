package io.turismo.backend.dto.social_media;

import io.turismo.backend.model.enums.SocialMediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SocialMediaCreateDTO(
    @NotBlank String socialMediaLink,
    @NotNull SocialMediaType socialMediaType
) {}
