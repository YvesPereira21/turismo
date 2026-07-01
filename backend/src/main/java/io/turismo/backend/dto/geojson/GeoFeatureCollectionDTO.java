package io.turismo.backend.dto.geojson;

import java.util.List;

public record GeoFeatureCollectionDTO<T>(
        String type,
        List<GeoFeatureDTO<T>> features
) {
    public GeoFeatureCollectionDTO(List<GeoFeatureDTO<T>> features) {
        this("FeatureCollection", features);
    }
}
