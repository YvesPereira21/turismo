package io.turismo.backend.dto.tour_guide;

import io.turismo.backend.dto.user.UserCreateDTO;
import io.turismo.backend.model.enums.TourGuideType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record TourGuideCreateDTO(
    @NotBlank String cadastur,
    @NotNull Boolean professional,
    @NotNull TourGuideType type,
    @Valid UserCreateDTO user,
    @Size(min = 1, max = 12) Set<String> languages
) {}
