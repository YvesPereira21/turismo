package io.turismo.backend.repository;

import io.turismo.backend.model.Warn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WarnRepository extends JpaRepository<Warn, UUID> {
    Page<Warn> findAllByTouristSpot_TouristSpotId(UUID touristSpotId, Pageable pageable);
}
