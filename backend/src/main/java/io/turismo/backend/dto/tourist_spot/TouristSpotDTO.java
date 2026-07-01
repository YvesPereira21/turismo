package io.turismo.backend.dto.tourist_spot;

import io.turismo.backend.config.PointToJsonSerializer;
import io.turismo.backend.dto.city.CityDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerSimpleDTO;
import io.turismo.backend.dto.tag.TagDTO;
import io.turismo.backend.dto.tour_guide.TourGuideSimpleDTO;
import io.turismo.backend.dto.warn.WarnDTO;
import org.locationtech.jts.geom.Point;
import tools.jackson.databind.annotation.JsonSerialize;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record TouristSpotDTO(
        UUID touristSpotId,
        String name,
        @JsonSerialize(using = PointToJsonSerializer.class)
        Point location,
        LocalTime opensAt,
        LocalTime closesAt,
        String shortDescription,
        String description,
        SpotManagerSimpleDTO spotManager,
        CityDTO city,
        List<WarnDTO> warns,
        List<TagDTO> tags,
        List<TourGuideSimpleDTO> tourGuides
) {}
