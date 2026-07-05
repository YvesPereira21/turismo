import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PhotoUpload } from '../../../core/models/photo';

@Injectable({
  providedIn: 'root'
})
export class PhotoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1`;

  uploadTouristSpotPhotos(photoUpload: PhotoUpload, touristSpotId: string): Observable<void> {
    const formData = new FormData();
    formData.append('photo', photoUpload.photo);
    formData.append('altText', photoUpload.altText);

    return this.http.post<void>(`${this.apiUrl}/tourist-spots/${touristSpotId}/photos`, formData);
  }

  uploadActivityPhoto(photoUpload: PhotoUpload, activityId: string): Observable<void> {
    const formData = new FormData();
    formData.append('photo', photoUpload.photo);
    formData.append('altText', photoUpload.altText);

    return this.http.post<void>(`${this.apiUrl}/activities/${activityId}/photos`, formData);
  }

  updateActivityPhoto(photoUpload: PhotoUpload, activityId: string): Observable<void> {
    const formData = new FormData();
    formData.append('photo', photoUpload.photo);
    formData.append('altText', photoUpload.altText);

    return this.http.put<void>(`${this.apiUrl}/activities/${activityId}/photos`, formData);
  }
}
