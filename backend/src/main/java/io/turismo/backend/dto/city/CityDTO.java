package io.turismo.backend.dto.city;

import java.util.UUID;

public record CityDTO(
    UUID cityId,
    String name,
    String stateName
) {}
