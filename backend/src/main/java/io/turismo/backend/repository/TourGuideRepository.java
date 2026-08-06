package io.turismo.backend.repository;

import io.turismo.backend.model.TourGuide;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface TourGuideRepository extends JpaRepository<TourGuide, UUID> {
    boolean existsByCadastur(String cadastur);
    Page<TourGuide> findAllByTouristSpots_TouristSpotId(UUID touristSpotId, Pageable pageable);
}
