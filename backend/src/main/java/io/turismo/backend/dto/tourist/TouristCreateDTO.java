package io.turismo.backend.dto.tourist;

import io.turismo.backend.dto.user.UserCreateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TouristCreateDTO(
        @NotNull LocalDate birthDate,
        @Valid UserCreateDTO user
) {}
