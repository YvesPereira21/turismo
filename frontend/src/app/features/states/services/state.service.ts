import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { State, StateCreate } from '../../../core/models/state';
import { Page } from '../../../core/models/page';

@Injectable({
  providedIn: 'root'
})
export class StateService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/states`;

  createState(state: StateCreate): Observable<State> {
    return this.http.post<State>(this.apiUrl, state);
  }

  getAllStates(): Observable<State[]> {
    return this.http.get<State[]>(this.apiUrl);
  }

  deleteState(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
