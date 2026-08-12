import { Photo } from './photo';

export interface ActivityCreate {
  name: string;
}

export interface Activity {
  activityId: string;
  name: string;
  photo?: Photo;
}
