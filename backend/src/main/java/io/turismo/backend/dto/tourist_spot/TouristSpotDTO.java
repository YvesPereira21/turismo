package io.turismo.backend.dto.tourist_spot;
import io.turismo.backend.dto.social_media.SocialMediaDTO;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.locationtech.jts.geom.Point;

import io.turismo.backend.config.PointToJsonSerializer;
import io.turismo.backend.dto.city.CityDTO;
import io.turismo.backend.dto.photo.PhotoDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerSimpleDTO;
import io.turismo.backend.dto.tag.TagDTO;
import io.turismo.backend.dto.tour_guide.TourGuideSimpleDTO;
import io.turismo.backend.dto.warn.WarnDTO;
import tools.jackson.databind.annotation.JsonSerialize;

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
        List<PhotoDTO> photos,
        List<WarnDTO> warns,
        List<TagDTO> tags,
        List<TourGuideSimpleDTO> tourGuides,
        List<SocialMediaDTO> socialsMedia
) {}
