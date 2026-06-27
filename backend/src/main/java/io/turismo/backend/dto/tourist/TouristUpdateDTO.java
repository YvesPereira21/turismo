package io.turismo.backend.dto.tourist;

import java.time.LocalDate;
import java.util.Set;

public record TouristUpdateDTO(
    LocalDate birthDate,
    String name,
    String phone,
    Set<String> languages
) {}
