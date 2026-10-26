import { Component, inject, OnInit, OnDestroy, signal } from '@angular/core';
import { LeafletModule } from '@bluehalo/ngx-leaflet';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import * as L from 'leaflet';
import { TouristSpotService } from '../../../features/tourist-spots/services/tourist-spot.service';
import { RouteService, RouteResult } from '../../../core/services/route.service';
import { GeoFeatureCollection } from '../../../core/models/geojson';
import { TouristSpotToMap } from '../../../core/models/tourist-spot';

export interface ActiveRouteInfo {
  destinationName: string;
  distanceKm: number;
  durationMinutes: number;
  durationFormatted: string;
  destLat: number;
  destLng: number;
}

@Component({
  selector: 'app-map-leaflet',
  standalone: true,
  imports: [LeafletModule],
  templateUrl: './map-leaflet.component.html',
  styleUrl: './map-leaflet.component.css'
})
export class MapLeafletComponent implements OnInit, OnDestroy {
  private touristSpotService = inject(TouristSpotService);
  private routeService: RouteService = inject(RouteService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  private querySub?: Subscription;
  private map?: L.Map;
  private currentRouteLayer?: L.GeoJSON;
  private userMarker?: L.Marker;
  private spotsGeoJson?: GeoFeatureCollection<TouristSpotToMap>;
  private pendingDestinationId: string | null = null;
  private pendingCoords?: { lat: number; lng: number; name: string };

  layers = signal<L.Layer[]>([]);
  routeInfo = signal<ActiveRouteInfo | null>(null);
  isCalculatingRoute = signal<boolean>(false);
  routeError = signal<string | null>(null);

  options: L.MapOptions = {
    layers: [
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
        attribution: '© OpenStreetMap contributors'
      })
    ],
    zoom: 8,
    minZoom: 5,
    center: L.latLng(-7.11532, -34.861),
    maxBounds: L.latLngBounds(
      L.latLng(-34.0, -74.0), // Southwest corner of Brazil
      L.latLng(5.5, -28.0)    // Northeast corner of Brazil
    ),
    maxBoundsViscosity: 1.0
  };

  ngOnInit() {
    this.loadTouristSpotsOnMap();
    this.listenToQueryParams();
  }

  ngOnDestroy() {
    this.querySub?.unsubscribe();
  }

  onMapReady(map: L.Map) {
    this.map = map;
    this.checkAndProcessPendingRoute();
  }

  private listenToQueryParams() {
    this.querySub = this.route.queryParams.subscribe((params) => {
      const destinationId = params['destinationId'];
      const destLat = params['destLat'];
      const destLng = params['destLng'];
      const destName = params['destName'];

      if (destLat && destLng) {
        this.isCalculatingRoute.set(true);
        this.pendingCoords = {
          lat: Number(destLat),
          lng: Number(destLng),
          name: destName || 'Ponto Turístico'
        };
      } else if (destinationId) {
        this.isCalculatingRoute.set(true);
        this.pendingDestinationId = destinationId;
      }

      this.checkAndProcessPendingRoute();
    });
  }

  private checkAndProcessPendingRoute() {
    if (!this.map) return;

    if (this.pendingCoords) {
      const coords = this.pendingCoords;
      this.pendingCoords = undefined;
      this.traceRoute(coords.lat, coords.lng, coords.name);
      return;
    }

    if (this.pendingDestinationId && this.spotsGeoJson) {
      const id = this.pendingDestinationId;
      this.pendingDestinationId = null;
      this.traceRouteById(id);
    }
  }

  loadTouristSpotsOnMap() {
    this.touristSpotService.getTouristSpotsToMap().subscribe({
      next: (touristSpots) => {
        this.spotsGeoJson = touristSpots;

        const geoJsonLayer = L.geoJSON(touristSpots as any, {
          onEachFeature: (feature: any, layer: any) => {
            const props = feature.properties;
            const popupContent = `
              <div class="text-center p-1 font-poppins">
                <h3 class="font-roboto-slab font-bold text-sm text-[#2C5C3E] mb-2">${props.name}</h3>
                <div class="flex items-center justify-center gap-2">
                  <a href="/tourist-spots/${props.touristSpotId}"
                    class="px-2.5 py-1.5 rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-700 text-xs font-medium transition-colors">
                    Detalhes
                  </a>
                  <button id="btn-trace-${props.touristSpotId}"
                    class="px-2.5 py-1.5 rounded-lg bg-[#2C5C3E] hover:bg-[#1f422c] text-white text-xs font-medium transition-colors">
                    Ver rota
                  </button>
                </div>
              </div>
            `;
            layer.bindPopup(popupContent);

            layer.on('popupopen', () => {
              const btn = document.getElementById(`btn-trace-${props.touristSpotId}`);
              if (btn) {
                btn.onclick = () => {
                  const [lng, lat] = feature.geometry.coordinates;
                  this.traceRoute(lat, lng, props.name);
                };
              }
            });
          }
        });

        this.layers.set([geoJsonLayer]);
        this.checkAndProcessPendingRoute();
      },
      error: (err: any) => {
        console.error('Erro ao carregar o GeoJSON do mapa:', err);
      }
    });
  }

  traceRouteById(spotId: string) {
    if (!this.spotsGeoJson) {
      this.pendingDestinationId = spotId;
      return;
    }

    const feature = this.spotsGeoJson.features?.find(
      (f: any) => String(f.properties?.touristSpotId).toLowerCase() === String(spotId).toLowerCase()
    );

    if (feature) {
      const [lng, lat] = feature.geometry.coordinates;
      const name = feature.properties?.name || this.route.snapshot.queryParams['destName'] || 'Ponto Turístico';
      this.traceRoute(lat, lng, name);
    } else {
      this.isCalculatingRoute.set(false);
      this.routeError.set('Ponto turístico selecionado não encontrado no mapa.');
    }
  }

  async traceRoute(destLat: number, destLng: number, destName: string) {
    this.isCalculatingRoute.set(true);
    this.routeError.set(null);

    try {
      const userCoords = await this.routeService.getCurrentLocation();

      this.routeService.getRoute(userCoords.latitude, userCoords.longitude, destLat, destLng).subscribe({
        next: (routeResult: RouteResult) => {
          if (!this.map) {
            this.isCalculatingRoute.set(false);
            return;
          }

          // Remove previous route and user layers
          if (this.currentRouteLayer) {
            this.map.removeLayer(this.currentRouteLayer);
            this.currentRouteLayer = undefined;
          }
          if (this.userMarker) {
            this.map.removeLayer(this.userMarker);
            this.userMarker = undefined;
          }

          // Modern pulsing icon for tourist current location
          const userIcon = L.divIcon({
            className: 'user-pulse-marker',
            html: `
              <div style="position: relative; width: 28px; height: 28px; display: flex; align-items: center; justify-content: center;">
                <span style="position: absolute; width: 100%; height: 100%; border-radius: 9999px; background-color: #3b82f6; opacity: 0.6; animation: ping 1.5s cubic-bezier(0, 0, 0.2, 1) infinite;"></span>
                <span style="position: relative; width: 16px; height: 16px; border-radius: 9999px; background-color: #1d4ed8; border: 3px solid #ffffff; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.3);"></span>
              </div>
            `,
            iconSize: [28, 28],
            iconAnchor: [14, 14]
          });

          this.userMarker = L.marker([userCoords.latitude, userCoords.longitude], { icon: userIcon })
            .addTo(this.map)
            .bindPopup('<strong style="font-family: Poppins, sans-serif; font-size: 12px; color: #1e3a8a;">Sua localização</strong>');

          // Draw road route line using forest green theme color
          this.currentRouteLayer = L.geoJSON(routeResult.geometry, {
            style: {
              color: '#2C5C3E',
              weight: 6,
              opacity: 0.85
            }
          }).addTo(this.map);

          // Adjust map camera bounds to fit the full route
          this.map.fitBounds(this.currentRouteLayer.getBounds(), {
            padding: [60, 60],
            maxZoom: 16
          });

          this.routeInfo.set({
            destinationName: destName,
            distanceKm: routeResult.distanceKm,
            durationMinutes: routeResult.durationMinutes,
            durationFormatted: routeResult.durationFormatted,
            destLat,
            destLng
          });

          this.isCalculatingRoute.set(false);
        },
        error: (err: any) => {
          this.isCalculatingRoute.set(false);
          this.routeError.set(err?.message || 'Não foi possível encontrar uma rota viária até o destino.');
        }
      });
    } catch (err: any) {
      this.isCalculatingRoute.set(false);
      this.routeError.set(err?.message || 'Não foi possível acessar sua localização.');
    }
  }

  clearRoute() {
    if (this.currentRouteLayer && this.map) {
      this.map.removeLayer(this.currentRouteLayer);
      this.currentRouteLayer = undefined;
    }
    if (this.userMarker && this.map) {
      this.map.removeLayer(this.userMarker);
      this.userMarker = undefined;
    }

    this.routeInfo.set(null);
    this.routeError.set(null);
    this.pendingDestinationId = null;
    this.pendingCoords = undefined;

    // Remove URL parameters without reloading page
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {},
      replaceUrl: true
    });
  }
}
