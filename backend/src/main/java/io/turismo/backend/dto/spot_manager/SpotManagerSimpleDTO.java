package io.turismo.backend.dto.spot_manager;


import io.turismo.backend.model.enums.ManagerType;

import java.util.List;
import java.util.UUID;

public record SpotManagerSimpleDTO(
        UUID spotManagerId,
        ManagerType managerType,
        String name,
        String phone
) {}
