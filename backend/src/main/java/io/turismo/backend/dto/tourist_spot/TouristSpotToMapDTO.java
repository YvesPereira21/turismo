package io.turismo.backend.dto.tourist_spot;

import java.util.UUID;

public record TouristSpotToMapDTO(
        UUID touristSpotId,
        String name
) {}
