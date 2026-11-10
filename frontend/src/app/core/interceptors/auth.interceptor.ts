import { HttpInterceptorFn, HttpErrorResponse, HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { catchError, finalize, Observable, shareReplay, switchMap, tap, throwError } from 'rxjs';
import { environment } from '../../environments/environment';

let refreshInFlight: Observable<{ accessToken: string }> | null = null;

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Do not intercept external API requests (prevents CORS issues and token leakage)
  if (!req.url.startsWith(environment.apiUrl)) {
    return next(req);
  }

  const authService = inject(AuthService);
  const http = inject(HttpClient);
  const router = inject(Router);
  const token = authService.getToken();

  let authReq = req;
  if (token) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // If 401 error and not a public auth request (/login, /refresh)
      if (error.status === 401 && !req.url.includes('/api/v1/auth/refresh') && !req.url.includes('/api/v1/auth/login')) {
        if (!refreshInFlight) {
          refreshInFlight = http.post<{ accessToken: string }>(
            `${environment.apiUrl}/api/v1/auth/refresh`,
            {},
            { withCredentials: true }
          ).pipe(
            tap(response => authService.saveToken(response.accessToken).subscribe()),
            catchError((refreshError: HttpErrorResponse) => {
              if (refreshError.status !== 429 && refreshError.status !== 503) {
                authService.logout();
                router.navigate(['/login']);
              }
              return throwError(() => refreshError);
            }),
            finalize(() => refreshInFlight = null),
            shareReplay({ bufferSize: 1, refCount: false })
          );
        }

        return refreshInFlight.pipe(
          switchMap(response => next(req.clone({
            setHeaders: { Authorization: `Bearer ${response.accessToken}` }
          })))
        );
      }

      return throwError(() => error);
    })
  );
};
