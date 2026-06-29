package io.turismo.backend.dto.spot_manager;

import io.turismo.backend.dto.user.UserCreateDTO;
import io.turismo.backend.dto.social_media.SocialMediaCreateDTO;
import io.turismo.backend.model.enums.ManagerType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SpotManagerCreateDTO(
    @NotNull ManagerType managerType,
    @Valid UserCreateDTO user,
    @Size(min = 1, max = 4) List<SocialMediaCreateDTO> socialsMedia
) {}
