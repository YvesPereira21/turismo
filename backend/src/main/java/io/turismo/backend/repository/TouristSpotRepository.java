package io.turismo.backend.repository;

import io.turismo.backend.model.TouristSpot;
import io.turismo.backend.model.enums.StateName;
import io.turismo.backend.repository.projection.TouristSpotNearbyProjection;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.UUID;

public interface TouristSpotRepository extends JpaRepository<TouristSpot, UUID> {
    Page<TouristSpot> findAllBySpotManager_SpotManagerId(UUID spotManagerId, Pageable pageable);
    @Query(
            "SELECT t FROM TouristSpot t " +
            "JOIN t.city c " +
            "JOIN c.state s " +
            "WHERE s.name = :stateName"
    )
    Page<TouristSpot> findAllByStateName(StateName stateName, Pageable pageable);
    @Query(
            "SELECT t, distance(:userLocation, t.location) AS distance FROM TouristSpot t " +
            "WHERE dwithin(:userLocation, t.location, :radius) = true"
    )
    Page<TouristSpotNearbyProjection> findAllPointsNearUserWithinCertainRadius(
            Point userLocation, double radius, Pageable pageable
    );
}
