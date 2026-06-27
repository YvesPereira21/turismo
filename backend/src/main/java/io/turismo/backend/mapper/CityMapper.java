package io.turismo.backend.mapper;

import io.turismo.backend.dto.city.CityCreateDTO;
import io.turismo.backend.dto.city.CityDTO;
import io.turismo.backend.model.City;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CityMapper {

    @Mapping(target = "cityId", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "touristSpots", ignore = true)
    City toEntity(CityCreateDTO dto);

    @Mapping(target = "stateName", source = "state.name")
    CityDTO toDto(City entity);
}
