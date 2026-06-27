package io.turismo.backend.mapper;

import io.turismo.backend.dto.tag.TagCreateUpdateDTO;
import io.turismo.backend.dto.tag.TagDTO;
import io.turismo.backend.model.Tag;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagDTO toDTO(Tag tag);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "touristSpots", ignore = true)
    void updateTagFromDTO(TagCreateUpdateDTO dto, @MappingTarget Tag tag);
}
