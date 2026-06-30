import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SocialMedia, SocialMediaCreate, SocialMediaUpdate } from '../../../core/models/social-media';
import { Page } from '../../../core/models/page';

@Injectable({
  providedIn: 'root'
})
export class SocialMediaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/socialsMedia`;

  createSocialMedia(spotManagerId: string, socialMedia: SocialMediaCreate): Observable<SocialMedia> {
    return this.http.post<SocialMedia>(`${this.apiUrl}/manager/${spotManagerId}`, socialMedia);
  }

  getAllSpotManagerSocialsMedia(spotManagerId: string, page = 0, size = 10): Observable<Page<SocialMedia>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<SocialMedia>>(`${this.apiUrl}/manager/${spotManagerId}`, { params });
  }

  updateSocialMedia(id: string, socialMedia: SocialMediaUpdate): Observable<SocialMedia> {
    return this.http.put<SocialMedia>(`${this.apiUrl}/${id}`, socialMedia);
  }

  deleteSocialMedia(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
