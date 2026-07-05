package io.turismo.backend.mapper;

import io.turismo.backend.dto.photo.PhotoDTO;
import io.turismo.backend.model.Photo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PhotoMapper {
    PhotoDTO toDTO(Photo photo);
}
