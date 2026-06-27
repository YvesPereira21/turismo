package io.turismo.backend.dto.social_media;

import io.turismo.backend.model.enums.SocialMediaType;

public record SocialMediaUpdateDTO(
        String socialMediaLink,
        SocialMediaType socialMediaType
) {}
