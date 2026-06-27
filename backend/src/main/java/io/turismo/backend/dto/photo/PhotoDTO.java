package io.turismo.backend.dto.photo;

import java.util.UUID;

public record PhotoDTO(
    UUID photoId,
    String url
) {}
