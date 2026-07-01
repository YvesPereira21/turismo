import { City } from './city';
import { SpotManagerSimple } from './spot-manager';
import { Tag } from './tag';
import { TourGuideSimple } from './tour-guide';
import { Warn } from './warn';

export interface Point {
  type: string;
  coordinates: number[];
}

export interface TouristSpotCreate {
  name: string;
  latitude: number;
  longitude: number;
  opensAt: string;
  closesAt: string;
  shortDescription: string;
  description: string;
  cityId: string;
  tags?: string[];
}

export interface TouristSpot {
  touristSpotId: string;
  name: string;
  location: Point;
  opensAt: string;
  closesAt: string;
  shortDescription: string;
  description: string;
  spotManager: SpotManagerSimple;
  city: City;
  warns: Warn[];
  tags: Tag[];
  tourGuides: TourGuideSimple[];
}

export interface TouristSpotList {
  touristSpotId: string;
  name: string;
  opensAt: string;
  closesAt: string;
  shortDescription: string;
  city: City;
  tags: Tag[];
  distance?: number;
}

export interface TouristSpotOnMap {
  touristSpotId: string;
  name: string;
  location: Point;
}

export interface TouristSpotToMap {
  touristSpotId: string;
  name: string;
}

export interface TouristSpotUpdate {
  name?: string;
  latitude?: number;
  longitude?: number;
  opensAt?: string;
  closesAt?: string;
  shortDescription?: string;
  description?: string;
  spotManagerId?: string;
  cityName?: string;
  tags?: string[];
}
