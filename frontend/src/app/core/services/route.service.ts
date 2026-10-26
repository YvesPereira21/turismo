import { inject, Injectable } from '@angular/core';
import { HttpBackend, HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface RouteResult {
  geometry: any;
  distanceKm: number;
  durationMinutes: number;
  durationFormatted: string;
}

export function formatDuration(durationMinutes: number): string {
  if (durationMinutes < 60) {
    return `${durationMinutes} min`;
  }
  const hours = Math.floor(durationMinutes / 60);
  const minutes = durationMinutes % 60;

  if (minutes === 0) {
    return hours === 1 ? '1 hora' : `${hours} horas`;
  }
  const hourLabel = hours === 1 ? '1 hora' : `${hours} horas`;
  return `${hourLabel} e ${minutes} min`;
}

export interface Coordinates {
  latitude: number;
  longitude: number;
}

@Injectable({
  providedIn: 'root'
})
export class RouteService {
  // Uses HttpBackend directly to bypass interceptors (avoids sending Authorization header to external API)
  private http = new HttpClient(inject(HttpBackend));

  /**
   * Obtains current tourist coordinates via browser Geolocation API.
   */
  getCurrentLocation(): Promise<Coordinates> {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error('Geolocalização não é suportada pelo seu navegador.'));
        return;
      }

      navigator.geolocation.getCurrentPosition(
        (position) => {
          resolve({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude
          });
        },
        (error) => {
          let message = 'Não foi possível obter sua localização.';
          if (error.code === error.PERMISSION_DENIED) {
            message = 'Permissão de localização negada. Ative o acesso à sua localização no navegador para traçar a rota.';
          } else if (error.code === error.POSITION_UNAVAILABLE) {
            message = 'Informações de GPS/localização indisponíveis no momento.';
          } else if (error.code === error.TIMEOUT) {
            message = 'Tempo limite esgotado ao tentar obter a localização.';
          }
          reject(new Error(message));
        },
        {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 60000
        }
      );
    });
  }

  /**
   * Queries OSRM (Open Source Routing Machine) to get the road route between origin and destination.
   */
  getRoute(startLat: number, startLng: number, destLat: number, destLng: number): Observable<RouteResult> {
    // Note: OSRM expects longitude,latitude order
    const url = `https://router.project-osrm.org/route/v1/driving/${startLng},${startLat};${destLng},${destLat}?overview=full&geometries=geojson`;

    return this.http.get<any>(url).pipe(
      map((res) => {
        if (!res || res.code !== 'Ok' || !res.routes || res.routes.length === 0) {
          throw new Error('Não foi possível encontrar uma rota viária até o destino selecionado.');
        }

        const route = res.routes[0];
        const distanceKm = Number((route.distance / 1000).toFixed(1));
        const durationMinutes = Math.max(1, Math.round(route.duration / 60));
        const durationFormatted = formatDuration(durationMinutes);

        return {
          geometry: route.geometry,
          distanceKm,
          durationMinutes,
          durationFormatted
        };
      })
    );
  }
}
