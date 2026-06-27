package io.turismo.backend.dto.spot_manager;

import io.turismo.backend.dto.user.UserCreateDTO;
import io.turismo.backend.dto.social_media.SocialMediaCreateDTO;
import io.turismo.backend.model.enums.ManagerType;
import java.util.List;

public record SpotManagerCreateDTO(
    String name,
    ManagerType managerType,
    String socialMediaLink,
    UserCreateDTO user,
    List<SocialMediaCreateDTO> socialsMedia
) {}
