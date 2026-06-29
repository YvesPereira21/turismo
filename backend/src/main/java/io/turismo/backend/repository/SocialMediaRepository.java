package io.turismo.backend.repository;

import io.turismo.backend.model.SocialMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SocialMediaRepository extends JpaRepository<SocialMedia, UUID> {
    List<SocialMedia> findAllBySpotManager_SpotManagerId(UUID spotManagerId);
}
