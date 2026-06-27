package io.turismo.backend.mapper;

import io.turismo.backend.dto.tour_guide.TourGuideCreateDTO;
import io.turismo.backend.dto.tour_guide.TourGuideDTO;
import io.turismo.backend.dto.tour_guide.TourGuideSimpleDTO;
import io.turismo.backend.model.TourGuide;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import io.turismo.backend.dto.tour_guide.TourGuideUpdateDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TourGuideMapper {

    @Mapping(target = "tourGuideId", ignore = true)
    @Mapping(target = "touristSpots", ignore = true)
    TourGuide toEntity(TourGuideCreateDTO dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "name", source = "user.name")
    TourGuideDTO toDTO(TourGuide entity);

    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "name", source = "user.name")
    TourGuideSimpleDTO toSimpleDTO(TourGuide entity);

    @Mapping(target = "cadastur", ignore = true)
    @Mapping(target = "professional", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "tourGuideId", ignore = true)
    @Mapping(target = "touristSpots", ignore = true)
    @Mapping(source = "name", target = "user.name")
    @Mapping(source = "phone", target = "user.phone")
    void updateEntityFromDto(TourGuideUpdateDTO dto, @MappingTarget TourGuide entity);
}
