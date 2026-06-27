package io.turismo.backend.dto.activity;

import java.util.UUID;

public record ActivityDTO(
    UUID activityId,
    String name
) {}
