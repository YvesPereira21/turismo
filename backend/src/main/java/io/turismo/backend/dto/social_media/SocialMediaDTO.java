package io.turismo.backend.dto.social_media;

import io.turismo.backend.model.enums.SocialMediaType;
import java.util.UUID;

public record SocialMediaDTO(
    UUID socialMediaId,
    String socialMediaLink,
    SocialMediaType socialMediaType
) {}
