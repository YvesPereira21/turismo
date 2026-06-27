package io.turismo.backend.dto.tourist_spot;

import org.locationtech.jts.geom.Point;

import java.time.LocalTime;
import java.util.UUID;

public record TouristSpotNearbyDTO(
    UUID touristSpotId,
    String name,
    Point location,
    LocalTime opensAt,
    LocalTime closesAt,
    String shortDescription,
    Double distance
) {}
