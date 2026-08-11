import { Component, effect, inject, input, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { environment } from '../../../../environments/environment';
import { TouristSpotService } from '../../services/tourist-spot.service';
import { TouristSpotFilters, TouristSpotList } from '../../../../core/models/tourist-spot';

@Component({
  selector: 'app-tourist-spot-list',
  imports: [RouterLink],
  templateUrl: './tourist-spot-list.component.html',
  styleUrl: './tourist-spot-list.component.css'
})
export class TouristSpotListComponent implements OnInit {
  private touristSpotService = inject(TouristSpotService);
  public mediaUrl = environment.mediaUrl;

  filters = input<TouristSpotFilters | null>(null);
  touristSpots = signal<TouristSpotList[]>([]);

  constructor() {
    effect(async () => {
      const filter = this.filters();

      let longitude: number | undefined;
      let latitude: number | undefined;

      if (filter?.distance) {
        try {
          const coordinate = await this.getLocation();
          longitude = coordinate.longitude;
          latitude = coordinate.latitude;
        } catch (error) {
          alert('Permita a localização para filtrar por distância');
          return;
        }
      }

      this.loadTouristSpots(
        filter?.name,
        filter?.cityName,
        filter?.stateName,
        filter?.tags,
        longitude,
        latitude,
        filter?.distance
      );
    });
  }

  ngOnInit(): void { }

  loadTouristSpots(
    name?: string | null,
    cityName?: string | null,
    stateName?: string | null,
    tags?: string[] | null,
    longitude?: number | null,
    latitude?: number | null,
    radius?: number | null
  ) {
    this.touristSpotService
      .getTouristSpots({
        name,
        cityName,
        stateName,
        tags,
        longitude,
        latitude,
        radius
      })
      .subscribe({
        next: (response) => {
          this.touristSpots.set(response.content);
        },
        error: (error) => {
          console.error('Não foi possível encontrar os pontos turísticos', error);
          this.touristSpots.set([]);
        }
      });
  }

  getLocation(): Promise<{ latitude: number; longitude: number }> {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject('Geolocalização não suportada');
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
          reject('Não foi possível obter a localização');
        }
      );
    });
  }
}
