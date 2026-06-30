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

  getCitiesFromState(stateName: string, page = 0, size = 10): Observable<Page<City>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<City>>(`${this.apiUrl}/state/${stateName}`, { params });
  }

  deleteCity(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
