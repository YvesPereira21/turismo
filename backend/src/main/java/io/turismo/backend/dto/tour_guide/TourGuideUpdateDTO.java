package io.turismo.backend.dto.tour_guide;

import io.turismo.backend.model.enums.TourGuideType;
import java.util.Set;

public record TourGuideUpdateDTO(
    String cadastur,
    Boolean professional,
    TourGuideType type,
    String name,
    String phone,
    Set<String> languages
) {}
