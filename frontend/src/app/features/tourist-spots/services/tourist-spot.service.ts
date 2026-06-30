import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TouristSpot, TouristSpotCreate, TouristSpotList, TouristSpotUpdate } from '../../../core/models/tourist-spot';
import { Page } from '../../../core/models/page';

@Injectable({
  providedIn: 'root'
})
export class TouristSpotService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/touristSpots`;

  createTouristSpot(touristSpot: TouristSpotCreate): Observable<TouristSpot> {
    return this.http.post<TouristSpot>(this.apiUrl, touristSpot);
  }

  getTouristSpot(id: string): Observable<TouristSpot> {
    return this.http.get<TouristSpot>(`${this.apiUrl}/${id}`);
  }

  getTouristSpots(page = 0, size = 10): Observable<Page<TouristSpotList>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<TouristSpotList>>(this.apiUrl, { params });
  }

  getTouristSpotsFromState(stateName: string, page = 0, size = 10): Observable<Page<TouristSpotList>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<TouristSpotList>>(`${this.apiUrl}/state/${stateName}`, { params });
  }

  getSpotManagerTouristSpots(spotManagerId: string, page = 0, size = 10): Observable<Page<TouristSpotList>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<TouristSpotList>>(`${this.apiUrl}/manager/${spotManagerId}`, { params });
  }

  getNearTouristSpots(longitude: number, latitude: number, radius: number, page = 0, size = 10): Observable<Page<TouristSpotList>> {
    const params = new HttpParams()
      .set('longitude', longitude.toString())
      .set('latitude', latitude.toString())
      .set('radius', radius.toString())
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<TouristSpotList>>(`${this.apiUrl}/near`, { params });
  }
}
