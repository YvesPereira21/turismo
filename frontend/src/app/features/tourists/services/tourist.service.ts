import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Tourist, TouristCreate, TouristUpdate } from '../../../core/models/tourist';

@Injectable({
  providedIn: 'root'
})
export class TouristService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/tourists`;

  createTourist(tourist: TouristCreate): Observable<Tourist> {
    return this.http.post<Tourist>(this.apiUrl, tourist);
  }

  getTourist(id: string): Observable<Tourist> {
    return this.http.get<Tourist>(`${this.apiUrl}/${id}`);
  }

  updateTourist(id: string, tourist: TouristUpdate): Observable<Tourist> {
    return this.http.put<Tourist>(`${this.apiUrl}/${id}`, tourist);
  }

  deleteTourist(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
