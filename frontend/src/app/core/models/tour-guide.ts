import { UserCreate } from './user';

export enum TourGuideType {
  REGIONAL = 'REGIONAL',
  NATIONAL = 'NATIONAL',
  SPECIALIZED = 'SPECIALIZED'
}

export interface TourGuideCreate {
  cadastur: string;
  type: string;
  user: UserCreate;
}

export interface TourGuide {
  tourGuideId: string;
  cadastur: string;
  type: TourGuideType;
  userId: string;
  name: string;
  phone: string;
}

export interface TourGuideSimple {
  tourGuideId: string;
  cadastur: string;
  type: TourGuideType;
  name: string;
  phone: string;
}

export interface TourGuideUpdate {
  cadastur?: string;
  type?: TourGuideType;
  name?: string;
  phone?: string;
}
