export interface GeoFeatureCollection<T> {
  type: 'FeatureCollection';
  features: GeoFeature<T>[];
}

export interface GeoFeature<T> {
  type: 'Feature';
  properties: T;
  geometry: {
    type: 'Point';
    coordinates: number[];
  };
}
