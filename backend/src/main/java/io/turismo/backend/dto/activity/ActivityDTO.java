package io.turismo.backend.dto.activity;

import io.turismo.backend.dto.photo.PhotoDTO;
import java.util.UUID;

public record ActivityDTO(
    UUID activityId,
    String name,
    PhotoDTO photo
) {}
