import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SpotManager, SpotManagerCreate, SpotManagerUpdate } from '../../../core/models/spot-manager';

@Injectable({
  providedIn: 'root'
})
export class SpotManagerService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/spotManagers`;

  createSpotManager(spotManager: SpotManagerCreate): Observable<SpotManager> {
    return this.http.post<SpotManager>(this.apiUrl, spotManager);
  }

  getSpotManager(id: string): Observable<SpotManager> {
    return this.http.get<SpotManager>(`${this.apiUrl}/${id}`);
  }

  currentSpotManager(): Observable<SpotManager> {
    return this.http.get<SpotManager>(`${this.apiUrl}/me`);
  }

  updateSpotManager(id: string, spotManager: SpotManagerUpdate): Observable<SpotManager> {
    return this.http.put<SpotManager>(`${this.apiUrl}/${id}`, spotManager);
  }

  deleteSpotManager(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
