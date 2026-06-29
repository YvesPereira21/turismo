package io.turismo.backend.dto.tour_guide;

import io.turismo.backend.model.enums.TourGuideType;

public record TourGuideUpdateDTO(
    String cadastur,
    TourGuideType type,
    String name,
    String phone
) {}
