import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Tag, TagCreateUpdate } from '../../../core/models/tag';
import { Page } from '../../../core/models/page';

@Injectable({
  providedIn: 'root'
})
export class TagService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/v1/tags`;

  createTag(tag: TagCreateUpdate): Observable<Tag> {
    return this.http.post<Tag>(this.apiUrl, tag);
  }

  getTagByName(name: string): Observable<Tag> {
    return this.http.get<Tag>(`${this.apiUrl}/${name}`);
  }

  getAllTags(page = 0, size = 10): Observable<Page<Tag>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<Tag>>(this.apiUrl, { params });
  }

  updateTag(id: string, tag: TagCreateUpdate): Observable<Tag> {
    return this.http.put<Tag>(`${this.apiUrl}/${id}`, tag);
  }

  deleteTagByTechnologyName(name: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${name}`);
  }
}
