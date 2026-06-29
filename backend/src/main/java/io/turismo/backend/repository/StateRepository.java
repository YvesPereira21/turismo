package io.turismo.backend.repository;

import io.turismo.backend.model.State;
import io.turismo.backend.model.enums.StateName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface StateRepository extends JpaRepository<State, UUID> {
    Optional<State> findByName(StateName name);
    boolean existsByNameIgnoreCase(StateName name);
}
