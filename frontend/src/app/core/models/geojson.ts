export interface GeoFeatureCollection<T> {
  type: string;
  features: GeoFeature<T>[];
}

export interface GeoFeature<T> {
  type: string;
  properties: T;
  geometry: {
    type: string;
    coordinates: number[];
  };
}
