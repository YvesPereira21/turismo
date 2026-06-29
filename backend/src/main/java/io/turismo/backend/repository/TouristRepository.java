package io.turismo.backend.repository;

import io.turismo.backend.model.Tourist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TouristRepository extends JpaRepository<Tourist, UUID> {
}
