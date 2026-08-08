import { Component, effect, inject, input, OnInit, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { TouristSpotService } from '../../services/tourist-spot.service';
import { TouristSpotList } from '../../../../core/models/tourist-spot';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-tourist-spot-list',
  imports: [RouterLink],
  templateUrl: './tourist-spot-list.component.html',
  styleUrl: './tourist-spot-list.component.css'
})
export class TouristSpotListComponent implements OnInit {
  private touristSpotService = inject(TouristSpotService);
  public mediaUrl = environment.mediaUrl;

  distance = input<number | null>(null);
  touristSpots = signal<TouristSpotList[]>([]);

  constructor() {
    effect(() => {
      const radius = this.distance();
      if (radius) {
        this.loadNearTouristSpots(radius);
      } else {
        this.loadTouristSpots();
      }
    });
  }

  ngOnInit(): void {
  }

  loadTouristSpots() {
    this.touristSpotService.getTouristSpots().subscribe({
      next: (response) => {
        this.touristSpots.set(response.content);
      },
      error: (error) => {
        alert("Não foi possível encontrar todos os pontos");
      }
    });
  }

  async loadNearTouristSpots(radius: number) {
    try {
      const coords = await this.getLocation();
      this.touristSpotService.getNearTouristSpots(coords.longitude, coords.latitude, radius).subscribe({
        next: (response) => {
          this.touristSpots.set(response.content);
        },
        error: (error) => {
          alert("Não foi possível carregar os pontos por distância");
        }
      });
    } catch (error) {
      alert("Você precisa permitir a geolocalização para filtrar por distância.");
    }
  }

  getLocation(): Promise<{ latitude: number, longitude: number }> {
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
