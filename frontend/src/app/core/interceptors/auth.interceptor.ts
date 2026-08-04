import { HttpInterceptorFn, HttpErrorResponse, HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { catchError, throwError, switchMap, BehaviorSubject, filter, take } from 'rxjs';
import { environment } from '../../environments/environment';

let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (req, next) => {
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
      // Se for erro 401 e não for requisição de auth pública (/login, /refresh)
      if (error.status === 401 && !req.url.includes('/api/v1/auth/refresh') && !req.url.includes('/api/v1/auth/login')) {
        if (!isRefreshing) {
          isRefreshing = true;
          refreshTokenSubject.next(null);

          return http.post<{ accessToken: string }>(
            `${environment.apiUrl}/api/v1/auth/refresh`,
            {},
            { withCredentials: true }
          ).pipe(
            switchMap((response) => {
              isRefreshing = false;
              authService.saveToken(response.accessToken);
              refreshTokenSubject.next(response.accessToken);

              return next(req.clone({
                setHeaders: {
                  Authorization: `Bearer ${response.accessToken}`
                }
              }));
            }),
            catchError((refreshError) => {
              isRefreshing = false;
              authService.logout();
              router.navigate(['/login']);
              return throwError(() => refreshError);
            })
          );
        } else {
          return refreshTokenSubject.pipe(
            filter(newToken => newToken !== null),
            take(1),
            switchMap(newToken => {
              return next(req.clone({
                setHeaders: {
                  Authorization: `Bearer ${newToken}`
                }
              }));
            })
          );
        }
      }

      return throwError(() => error);
    })
  );
};
