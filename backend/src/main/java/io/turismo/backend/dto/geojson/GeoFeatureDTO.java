package io.turismo.backend.dto.geojson;

import io.turismo.backend.config.PointToJsonSerializer;
import org.locationtech.jts.geom.Point;
import tools.jackson.databind.annotation.JsonSerialize;

public record GeoFeatureDTO<T>(
        String type,
        T properties,
        @JsonSerialize(using = PointToJsonSerializer.class)
        Point geometry
) {
    public GeoFeatureDTO(T properties, Point geometry) {
        this("Feature", properties, geometry);
    }
}
