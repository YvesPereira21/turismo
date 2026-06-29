package io.turismo.backend.repository;

import io.turismo.backend.model.SpotManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpotManagerRepository extends JpaRepository<SpotManager, UUID> {
}
