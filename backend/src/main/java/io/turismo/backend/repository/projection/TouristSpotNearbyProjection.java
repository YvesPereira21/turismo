package io.turismo.backend.repository.projection;

import io.turismo.backend.model.TouristSpot;

public interface TouristSpotNearbyProjection {
    TouristSpot getTouristSpot();
    Double getDistance();
}
