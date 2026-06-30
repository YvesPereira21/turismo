import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Activity, ActivityCreate } from '../../../core/models/activity';
import { Page } from '../../../core/models/page';

@Injectable({
  providedIn: 'root'
})
export class ActivityService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/activities`;

  createActivity(touristSpotId: string, activity: ActivityCreate): Observable<Activity> {
    return this.http.post<Activity>(`${this.apiUrl}/touristSpot/${touristSpotId}`, activity);
  }

  getActivity(id: string): Observable<Activity> {
    return this.http.get<Activity>(`${this.apiUrl}/${id}`);
  }

  getAllTouristSpotActivities(touristSpotId: string, page = 0, size = 10): Observable<Page<Activity>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<Activity>>(`${this.apiUrl}/touristSpot/${touristSpotId}`, { params });
  }

  updateActivity(id: string, activity: ActivityCreate): Observable<Activity> {
    return this.http.put<Activity>(`${this.apiUrl}/${id}`, activity);
  }

  deleteActivity(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
