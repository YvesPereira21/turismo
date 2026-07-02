import { SocialMedia, SocialMediaCreate } from './social-media';
import { UserCreate } from './user';

export enum ManagerType {
  PRIVATE = 'PRIVATE',
  PUBLIC = 'PUBLIC',
  NGO = 'NGO',
  COMMUNITY = 'COMMUNITY'
}

export interface SpotManagerCreate {
  managerType: string;
  user: UserCreate;
  socialsMedia: SocialMediaCreate[];
}

export interface SpotManager {
  spotManagerId: string;
  userId: string;
  managerType: ManagerType;
  name: string;
  phone: string;
  socialsMedia: SocialMedia[];
}

export interface SpotManagerSimple {
  spotManagerId: string;
  managerType: ManagerType;
  name: string;
  phone: string;
  socialsMedia: SocialMedia[];
}

export interface SpotManagerUpdate {
  name: string;
  phone: string;
  managerType: ManagerType;
  socialMediaLink: string;
  socialsMedia: SocialMediaCreate[];
}
