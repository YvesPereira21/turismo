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

  getAllStates(page = 0, size = 10): Observable<Page<State>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<State>>(this.apiUrl, { params });
  }

  deleteState(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
