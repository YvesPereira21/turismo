export interface Photo {
  photoId: string;
  url: string;
  altText: string;
}

export interface PhotoUpload {
  photo: File;
  altText: string;
}
