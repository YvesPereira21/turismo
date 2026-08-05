package io.turismo.backend.dto.spot_manager;

import io.turismo.backend.dto.user.UserCreateDTO;

import io.turismo.backend.model.enums.ManagerType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SpotManagerCreateDTO(
    @NotNull ManagerType managerType,
    @Valid UserCreateDTO user
) {}
