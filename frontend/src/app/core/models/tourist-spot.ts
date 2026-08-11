import { City } from './city';
import { Photo } from './photo';
import { SpotManagerSimple } from './spot-manager';
import { Tag } from './tag';
import { TourGuideSimple } from './tour-guide';
import { Warn } from './warn';
import { SocialMedia, SocialMediaCreate } from './social-media';

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
  socialsMedia?: SocialMediaCreate[];
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
  photos: Photo[];
  warns: Warn[];
  tags: Tag[];
  tourGuides: TourGuideSimple[];
  socialsMedia: SocialMedia[];
}

export interface TouristSpotList {
  touristSpotId: string;
  name: string;
  opensAt: string;
  closesAt: string;
  shortDescription: string;
  city: City;
  tags: Tag[];
  photos: Photo[];
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
  cityName?: string;
  tags?: string[];
  socialsMedia?: SocialMediaCreate[];
}

export interface TouristSpotFilters {
  name?: string | null;
  cityName?: string | null;
  stateName?: string | null;
  tags?: string[] | null;
  longitude?: number | null;
  latitude?: number | null;
  radius?: number | null;
  distance?: number | null;
}
