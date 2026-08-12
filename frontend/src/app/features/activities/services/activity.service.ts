import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Activity, ActivityCreate } from '../../../core/models/activity';

@Injectable({
  providedIn: 'root'
})
export class ActivityService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/api/v1`;

  getActivitiesByTouristSpotId(touristSpotId: string): Observable<Activity[]> {
    return this.http.get<Activity[]>(`${this.baseUrl}/tourist-spots/${touristSpotId}/activities`);
  }

  createActivity(touristSpotId: string, activity: ActivityCreate): Observable<Activity> {
    return this.http.post<Activity>(`${this.baseUrl}/tourist-spots/${touristSpotId}/activities`, activity);
  }

  updateActivity(activityId: string, activity: ActivityCreate): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/activities/${activityId}`, activity);
  }

  deleteActivity(activityId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/activities/${activityId}`);
  }
}
