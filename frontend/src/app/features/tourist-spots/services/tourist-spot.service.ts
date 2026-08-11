import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TouristSpot, TouristSpotCreate, TouristSpotList, TouristSpotUpdate, TouristSpotToMap, TouristSpotFilters } from '../../../core/models/tourist-spot';
import { GeoFeatureCollection } from '../../../core/models/geojson';
import { Page } from '../../../core/models/page';

@Injectable({
  providedIn: 'root'
})
export class TouristSpotService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/tourist-spots`;

  createTouristSpot(touristSpot: TouristSpotCreate): Observable<TouristSpot> {
    return this.http.post<TouristSpot>(this.apiUrl, touristSpot);
  }

  getTouristSpot(id: string): Observable<TouristSpot> {
    return this.http.get<TouristSpot>(`${this.apiUrl}/${id}`);
  }

  getTouristSpotsToMap(): Observable<GeoFeatureCollection<TouristSpotToMap>> {
    return this.http.get<GeoFeatureCollection<TouristSpotToMap>>(`${environment.apiUrl}/api/v1/spots-to-map/`);
  }

  getTouristSpots(filters?: TouristSpotFilters, page = 0, size = 10): Observable<Page<TouristSpotList>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filters?.name) params = params.set('name', filters.name);
    if (filters?.cityName) params = params.set('cityName', filters.cityName);
    if (filters?.stateName) params = params.set('stateName', filters.stateName);
    if (filters?.longitude != null) params = params.set('longitude', filters.longitude.toString());
    if (filters?.latitude != null) params = params.set('latitude', filters.latitude.toString());
    if (filters?.radius != null) params = params.set('radius', filters.radius.toString());

    if (filters?.tags?.length) {
      filters.tags.forEach(tag => {
        params = params.append('tags', tag);
      });
    }

    return this.http.get<Page<TouristSpotList>>(this.apiUrl, { params });
  }

  getSpotManagerTouristSpots(spotManagerId: string, page = 0, size = 10): Observable<Page<TouristSpotList>> {
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get<Page<TouristSpotList>>(`${environment.apiUrl}/api/v1/manager/${spotManagerId}/all-tourist-spots`, { params });
  }

  updateTouristSpot(id: string, touristSpot: TouristSpotUpdate): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, touristSpot);
  }

  deleteTouristSpot(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
