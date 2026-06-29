package io.turismo.backend.repository;

import io.turismo.backend.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    boolean existsByName(String name);
    Optional<Tag> findByName(String name);
    Optional<Tag> findByNameContainingIgnoreCase(String name);
}

