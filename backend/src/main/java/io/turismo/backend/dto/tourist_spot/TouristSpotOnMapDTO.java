package io.turismo.backend.dto.tourist_spot;

import org.locationtech.jts.geom.Point;
import java.util.UUID;

public record TouristSpotOnMapDTO(
        UUID touristSpotId,
        String name,
        Point location
) {}
