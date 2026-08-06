import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TourGuide, TourGuideCreate, TourGuideUpdate } from '../../../core/models/tour-guide';
import { Page } from '../../../core/models/page';

@Injectable({
  providedIn: 'root'
})
export class TourGuideService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/tour-guides`;

  createTourGuide(tourGuide: TourGuideCreate): Observable<TourGuide> {
    return this.http.post<TourGuide>(this.apiUrl, tourGuide);
  }

  getTourGuide(id: string): Observable<TourGuide> {
    return this.http.get<TourGuide>(`${this.apiUrl}/${id}`);
  }

  getTourGuidesByTouristSpot(touristSpotId: string, page = 0, size = 10): Observable<Page<TourGuide>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<TourGuide>>(`${environment.apiUrl}/api/v1/tourist-spots/${touristSpotId}/tour-guides`, { params });
  }

  updateTourGuide(id: string, tourGuide: TourGuideUpdate): Observable<TourGuide> {
    return this.http.put<TourGuide>(`${this.apiUrl}/${id}`, tourGuide);
  }

  deleteTourGuide(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
