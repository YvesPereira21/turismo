package io.turismo.backend.mapper;

import io.turismo.backend.dto.tourist.TouristDTO;
import io.turismo.backend.model.Tourist;
import io.turismo.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import io.turismo.backend.dto.tourist.TouristUpdateDTO;

@Mapper(componentModel = "spring")
public interface TouristMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "phone", source = "user.phone")
    TouristDTO toDto(Tourist entity);

    @Mapping(target = "touristId", ignore = true)
    @Mapping(source = "name", target = "user.name")
    @Mapping(source = "phone", target = "user.phone")
    void updateEntityFromDto(TouristUpdateDTO dto, @MappingTarget Tourist entity);
}
