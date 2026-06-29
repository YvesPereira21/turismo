package io.turismo.backend.repository;

import io.turismo.backend.model.TourGuide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TourGuideRepository extends JpaRepository<TourGuide, UUID> {
    boolean existsByCadastur(String cadastur);
}
