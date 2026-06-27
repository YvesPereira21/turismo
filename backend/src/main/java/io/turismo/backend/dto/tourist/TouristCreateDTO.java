package io.turismo.backend.dto.tourist;

import io.turismo.backend.dto.user.UserCreateDTO;

import java.time.LocalDate;
import java.util.Set;

public record TouristCreateDTO(
        LocalDate birthDate,
        UserCreateDTO user,
        Set<String> languages
) {}
