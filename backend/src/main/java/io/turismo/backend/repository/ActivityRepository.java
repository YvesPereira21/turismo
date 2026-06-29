package io.turismo.backend.repository;

import io.turismo.backend.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
}
