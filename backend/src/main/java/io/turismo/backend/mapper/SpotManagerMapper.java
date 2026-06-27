package io.turismo.backend.mapper;

import io.turismo.backend.dto.spot_manager.SpotManagerCreateDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerSimpleDTO;
import io.turismo.backend.model.SpotManager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import io.turismo.backend.dto.spot_manager.SpotManagerUpdateDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, uses = {SocialMediaMapper.class})
public interface SpotManagerMapper {

    @Mapping(target = "spotManagerId", ignore = true)
    @Mapping(target = "touristSpots", ignore = true)
    @Mapping(target = "socialsMedia", ignore = true)
    SpotManager toEntity(SpotManagerCreateDTO dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "name", source = "user.name")
    SpotManagerDTO toDTO(SpotManager entity);

    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "name", source = "user.name")
    SpotManagerSimpleDTO toSimpleDTO(SpotManager entity);

    @Mapping(target = "managerType", ignore = true)
    @Mapping(target = "spotManagerId", ignore = true)
    @Mapping(target = "touristSpots", ignore = true)
    @Mapping(target = "socialsMedia", ignore = true)
    @Mapping(source = "name", target = "user.name")
    @Mapping(source = "phone", target = "user.phone")
    void updateEntityFromDto(SpotManagerUpdateDTO dto, @MappingTarget SpotManager entity);
}
