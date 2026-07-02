import { Component, inject, OnInit, signal } from '@angular/core';
import { LeafletModule } from '@bluehalo/ngx-leaflet';
import * as L from 'leaflet';
import { TouristSpotService } from '../../../features/tourist-spots/services/tourist-spot.service';

@Component({
  selector: 'app-map-leaflet',
  imports: [LeafletModule],
  templateUrl: './map-leaflet.component.html',
  styleUrl: './map-leaflet.component.css'
})
export class MapLeafletComponent implements OnInit {
  private touristSpotService = inject(TouristSpotService);

  layers = signal<L.Layer[]>([]);

  options: L.MapOptions = {
    layers: [
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
        attribution: '© OpenStreetMap contributors'
      })
    ],
    zoom: 10,
    center: L.latLng(-7.11532, -34.861)
  };

  ngOnInit() {
    this.loadTouristSpotsOnMap();
  }

  loadTouristSpotsOnMap() {
    this.touristSpotService.getTouristSpotsToMap().subscribe({
      next: (touristSpot) => {
        const geoJsonLayer = L.geoJSON(touristSpot as any).bindPopup((layer: any) => {
          const props = layer.feature.properties;

          return `
            <div class="text-center">
              <h3 class="font-semibold text-lg text-[#2C5C3E] mb-2">${props.name}</h3>
              <a href="/tourist-spots/${props.touristSpotId}"
                class="btn btn-sm text-xs" 
                style="background-color: #2C5C3E; color: white">
                Ver detalhes
              </a>
            </div>
          `;
        });

        this.layers.set([geoJsonLayer]);
      },
      error: (err) => {
        console.error('Erro ao carregar o GeoJSON do mapa:', err);
      }
    });
  }
}
