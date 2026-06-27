package io.turismo.backend.mapper;

import io.turismo.backend.dto.tourist_spot.TouristSpotCreateDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotNearbyDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotUpdateDTO;
import io.turismo.backend.model.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {CityMapper.class, SpotManagerMapper.class, WarnMapper.class, TagMapper.class, TourGuideMapper.class},
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface TouristSpotMapper {

    @Mapping(target = "touristSpotId", ignore = true)
    @Mapping(target = "location", expression = "java(mapLatLngToPoint(dto.latitude(), dto.longitude()))")
    @Mapping(target = "spotManager", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "activities", ignore = true)
    @Mapping(target = "warns", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "tourGuides", ignore = true)
    TouristSpot toEntity(TouristSpotCreateDTO dto);

    TouristSpotDTO toDto(TouristSpot entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "touristSpotId", ignore = true)
    @Mapping(target = "location", expression = "java(mapLatLngToPoint(dto.latitude(), dto.longitude()))")
    @Mapping(target = "spotManager", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "activities", ignore = true)
    @Mapping(target = "warns", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "tourGuides", ignore = true)
    void updateEntityFromDto(TouristSpotUpdateDTO dto, @MappingTarget TouristSpot entity);

    TouristSpotNearbyDTO toNearbyDto(TouristSpot entity, Double distance);

    default Point mapLatLngToPoint(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
