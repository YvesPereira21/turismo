package io.turismo.backend.mapper;

import io.turismo.backend.dto.state.StateDTO;
import io.turismo.backend.model.State;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StateMapper {

    StateDTO toDTO(State state);
}
