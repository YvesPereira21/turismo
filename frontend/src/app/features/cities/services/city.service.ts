import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { City, CityCreate } from '../../../core/models/city';
import { Page } from '../../../core/models/page';

@Injectable({
  providedIn: 'root'
})
export class CityService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/cities`;

  createCity(city: CityCreate): Observable<City> {
    return this.http.post<City>(this.apiUrl, city);
  }

  getCity(id: string): Observable<City> {
    return this.http.get<City>(`${this.apiUrl}/${id}`);
  }

  getCitiesFromState(stateName: string): Observable<City[]> {
    return this.http.get<City[]>(`${environment.apiUrl}/api/v1/state/${stateName}/cities`);
  }

  deleteCity(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
