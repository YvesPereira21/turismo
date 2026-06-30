import { UserCreate } from './user';

export interface TouristCreate {
  birthDate: string;
  user: UserCreate;
}

export interface Tourist {
  touristId: string;
  birthDate: string;
  userId: string;
  name: string;
  phone: string;
}

export interface TouristUpdate {
  birthDate?: string;
  name?: string;
  phone?: string;
  languages?: string[];
}
