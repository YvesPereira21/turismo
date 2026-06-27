package io.turismo.backend.dto.spot_manager;

import io.turismo.backend.dto.social_media.SocialMediaCreateDTO;
import io.turismo.backend.model.enums.ManagerType;
import java.util.List;

public record SpotManagerUpdateDTO(
    String name,
    String phone,
    ManagerType managerType,
    String socialMediaLink,
    List<SocialMediaCreateDTO> socialsMedia
) {}
