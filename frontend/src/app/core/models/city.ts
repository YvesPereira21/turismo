import { StateName } from './state';

export interface CityCreate {
  name: string;
  stateName: StateName;
}

export interface City {
  cityId: string;
  name: string;
  stateName: StateName;
}
