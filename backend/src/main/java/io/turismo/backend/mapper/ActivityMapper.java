package io.turismo.backend.mapper;

import io.turismo.backend.dto.activity.ActivityCreateDTO;
import io.turismo.backend.dto.activity.ActivityDTO;
import io.turismo.backend.model.Activity;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {PhotoMapper.class},
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface ActivityMapper {

    @Mapping(target = "activityId", ignore = true)
    @Mapping(target = "touristSpot", ignore = true)
    @Mapping(target = "photo", ignore = true)
    Activity toEntity(ActivityCreateDTO dto);

    ActivityDTO toDTO(Activity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "activityId", ignore = true)
    @Mapping(target = "touristSpot", ignore = true)
    @Mapping(target = "photo", ignore = true)
    void updateEntityFromDTO(ActivityCreateDTO dto, @MappingTarget Activity entity);
}
