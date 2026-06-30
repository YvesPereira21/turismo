export enum SocialMediaType {
  INSTAGRAM = 'INSTAGRAM',
  FACEBOOK = 'FACEBOOK',
  X = 'X'
}

export interface SocialMediaCreate {
  socialMediaLink: string;
  socialMediaType: SocialMediaType;
}

export interface SocialMedia {
  socialMediaId: string;
  socialMediaLink: string;
  socialMediaType: SocialMediaType;
}

export interface SocialMediaUpdate {
  socialMediaLink: string;
}
