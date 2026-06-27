package io.turismo.backend.mapper;

import io.turismo.backend.dto.warn.WarnCreateDTO;
import io.turismo.backend.dto.warn.WarnDTO;
import io.turismo.backend.model.Warn;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WarnMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventDate", ignore = true)
    @Mapping(target = "touristSpot", ignore = true)
    Warn toEntity(WarnCreateDTO dto);

    WarnDTO toDTO(Warn w);
}
