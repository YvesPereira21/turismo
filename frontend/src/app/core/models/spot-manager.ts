
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
}

export interface SpotManager {
  spotManagerId: string;
  userId: string;
  managerType: ManagerType;
  name: string;
  phone: string;
}

export interface SpotManagerSimple {
  spotManagerId: string;
  managerType: ManagerType;
  name: string;
  phone: string;
}

export interface SpotManagerUpdate {
  name: string;
  phone: string;
  managerType: ManagerType;
}
