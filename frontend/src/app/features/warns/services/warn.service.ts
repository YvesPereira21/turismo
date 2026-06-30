import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Warn, WarnCreate } from '../../../core/models/warn';
import { Page } from '../../../core/models/page';

@Injectable({
  providedIn: 'root'
})
export class WarnService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/warns`;

  createWarn(touristSpotId: string, warn: WarnCreate): Observable<Warn> {
    return this.http.post<Warn>(`${this.apiUrl}/touristSpot/${touristSpotId}`, warn);
  }

  getWarn(id: string): Observable<Warn> {
    return this.http.get<Warn>(`${this.apiUrl}/${id}`);
  }

  getAllTouristSpotWarn(touristSpotId: string, page = 0, size = 10): Observable<Page<Warn>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<Warn>>(`${this.apiUrl}/touristSpot/${touristSpotId}`, { params });
  }

  deleteWarn(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
