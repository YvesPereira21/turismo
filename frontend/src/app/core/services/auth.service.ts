import { Injectable, signal, computed, inject } from '@angular/core';
import { Router } from '@angular/router';
import { tap, catchError, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface UserResponse {
  id: string;
  name: string;
  email: string;
  phone?: string;
  role?: string;
  spotManagerId?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private tokenSignal = signal<string | null>(null);
  private userSignal = signal<UserResponse | null>(null);

  public token = computed(() => this.tokenSignal());
  public isAuthenticated = computed(() => !!this.tokenSignal());
  public currentUser = computed(() => this.userSignal());
  public userRole = computed(() => this.userSignal()?.role);
  public isAdmin = computed(() => this.userSignal()?.role === 'ADMIN');

  login(credentials: { email?: string | null; password?: string | null }) {
    return this.http.post<{ accessToken: string; user: UserResponse }>(
      `${environment.apiUrl}/api/v1/auth/login`,
      credentials,
      { withCredentials: true }
    ).pipe(
      tap((response) => {
        this.saveToken(response.accessToken, response.user);
      })
    );
  }
  saveToken(token: string, user?: UserResponse): void {
    this.tokenSignal.set(token);
    if (user) {
      this.userSignal.set(user);
    } else {
      this.loadCurrentUser();
    }
  }

  getToken(): string | null {
    return this.tokenSignal();
  }

  loadCurrentUser(): void {
    this.http.get<UserResponse>(`${environment.apiUrl}/api/v1/auth/me`).subscribe({
      next: (user) => this.userSignal.set(user),
      error: () => this.clearSession()
    });
  }

  restoreSession() {
    return this.http.post<{ accessToken: string }>(
      `${environment.apiUrl}/api/v1/auth/refresh`,
      {},
      { withCredentials: true }
    ).pipe(
      tap((response) => {
        this.saveToken(response.accessToken);
      }),
      catchError(() => {
        this.clearSession();
        return of(null);
      })
    );
  }

  isOwner(targetUserId?: string): boolean {
    const user = this.userSignal();
    if (!user || !targetUserId) return false;
    return user.id === targetUserId;
  }

  logout(): void {
    this.http.post(`${environment.apiUrl}/api/v1/auth/logout`, {}, { withCredentials: true }).subscribe({
      next: () => {
        this.clearSession();
        this.router.navigate(['/login']);
      },
      error: () => {
        this.clearSession();
        this.router.navigate(['/login']);
      }
    });
  }

  private clearSession(): void {
    this.tokenSignal.set(null);
    this.userSignal.set(null);
  }
}
