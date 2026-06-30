export enum UserRole {
  TOURIST = 'TOURIST',
  SPOTMANAGER = 'SPOTMANAGER',
  TOURGUIDE = 'TOURGUIDE',
  ADMIN = 'ADMIN'
}

export interface UserCreate {
  name: string;
  email: string;
  password?: string;
  phone?: string;
}

export interface User {
  id: string;
  name: string;
  email: string;
  phone?: string;
}

export interface UserLogin {
  email: string;
  password?: string;
}
