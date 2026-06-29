package io.turismo.backend.dto.tour_guide;

import io.turismo.backend.model.enums.TourGuideType;
import java.util.Set;
import java.util.UUID;

public record TourGuideSimpleDTO(
        UUID tourGuideId,
        String cadastur,
        TourGuideType type,
        String name,
        String phone
) {}
