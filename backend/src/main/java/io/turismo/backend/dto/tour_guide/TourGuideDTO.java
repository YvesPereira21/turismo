package io.turismo.backend.dto.tour_guide;

import io.turismo.backend.model.enums.TourGuideType;
import java.util.Set;
import java.util.UUID;

public record TourGuideDTO(
    UUID tourGuideId,
    String cadastur,
    TourGuideType type,
    UUID userId,
    String name,
    String phone
) {}
