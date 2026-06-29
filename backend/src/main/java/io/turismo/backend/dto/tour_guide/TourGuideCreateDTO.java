package io.turismo.backend.dto.tour_guide;

import io.turismo.backend.dto.user.UserCreateDTO;
import io.turismo.backend.model.enums.TourGuideType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TourGuideCreateDTO(
    @NotBlank String cadastur,
    @NotNull TourGuideType type,
    @Valid UserCreateDTO user
) {}
