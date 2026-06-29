package io.turismo.backend.dto.tourist_spot;

import io.turismo.backend.dto.city.CityDTO;
import io.turismo.backend.dto.tag.TagDTO;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record TouristSpotListDTO(
        UUID touristSpotId,
        String name,
        LocalTime opensAt,
        LocalTime closesAt,
        String shortDescription,
        CityDTO city,
        List<TagDTO> tags,
        Double distance
) {}
