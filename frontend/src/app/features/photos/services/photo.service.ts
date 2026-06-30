import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PhotoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/photos`;

  uploadTouristSpotsPhotos(touristSpotId: string, photos: File[]): Observable<void> {
    const formData = new FormData();
    photos.forEach(photo => formData.append('photos', photo));
    return this.http.post<void>(`${this.apiUrl}/touristSpot/${touristSpotId}`, formData);
  }

  uploadActivityPhoto(activityId: string, photo: File): Observable<void> {
    const formData = new FormData();
    formData.append('photo', photo);
    return this.http.post<void>(`${this.apiUrl}/activity/${activityId}`, formData);
  }

  updateActivityPhoto(activityId: string, photo: File): Observable<void> {
    const formData = new FormData();
    formData.append('photo', photo);
    return this.http.put<void>(`${this.apiUrl}/activity/${activityId}`, formData);
  }
}
