package io.turismo.backend.mapper;

import io.turismo.backend.dto.social_media.SocialMediaCreateDTO;
import io.turismo.backend.dto.social_media.SocialMediaDTO;
import io.turismo.backend.dto.social_media.SocialMediaUpdateDTO;
import io.turismo.backend.model.SocialMedia;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SocialMediaMapper {

    @Mapping(target = "socialMediaId", ignore = true)
    @Mapping(target = "spotManager", ignore = true)
    SocialMedia toEntity(SocialMediaCreateDTO dto);

    SocialMediaDTO toDTO(SocialMedia s);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "socialMediaId", ignore = true)
    @Mapping(target = "socialMediaType", ignore = true)
    @Mapping(target = "spotManager", ignore = true)
    void updateEntityFromDto(SocialMediaUpdateDTO dto, @MappingTarget SocialMedia entity);
}
