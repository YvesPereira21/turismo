package io.turismo.backend.repository;

import io.turismo.backend.model.State;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface StateRepository extends JpaRepository<State, UUID> {
    Optional<State> findByName(String name);
    boolean existsByNameIgnoreCase(String name);
}
