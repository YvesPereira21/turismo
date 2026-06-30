import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TourGuide, TourGuideCreate, TourGuideUpdate } from '../../../core/models/tour-guide';

@Injectable({
  providedIn: 'root'
})
export class TourGuideService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/tourGuides`;

  createTourGuide(tourGuide: TourGuideCreate): Observable<TourGuide> {
    return this.http.post<TourGuide>(this.apiUrl, tourGuide);
  }

  getTourGuide(id: string): Observable<TourGuide> {
    return this.http.get<TourGuide>(`${this.apiUrl}/${id}`);
  }

  updateTourGuide(id: string, tourGuide: TourGuideUpdate): Observable<TourGuide> {
    return this.http.put<TourGuide>(`${this.apiUrl}/${id}`, tourGuide);
  }

  deleteTourGuide(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
