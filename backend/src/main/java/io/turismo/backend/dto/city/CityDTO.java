package io.turismo.backend.dto.city;

import io.turismo.backend.model.enums.StateName;

import java.util.UUID;

public record CityDTO(
    UUID cityId,
    String name,
    StateName stateName
) {}
