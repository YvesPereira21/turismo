import { Component, inject, OnInit, signal } from '@angular/core';
import { MapLeafletComponent } from '../../../../shared/components/map-leaflet/map-leaflet.component';
import { TouristSpotService } from '../../../tourist-spots/services/tourist-spot.service';
import { TouristSpotList } from '../../../../core/models/tourist-spot';
import { environment } from '../../../../environments/environment';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-map-view',
  imports: [MapLeafletComponent, RouterLink],
  templateUrl: './map-view.component.html',
  styleUrl: './map-view.component.css'
})
export class MapViewComponent implements OnInit {
  private touristSpotService = inject(TouristSpotService);
  public mediaUrl = environment.mediaUrl;

  touristSpots = signal<TouristSpotList[]>([]);

  ngOnInit() {
    this.loadTouristSpots();
  }

  loadTouristSpots() {
    this.touristSpotService.getTouristSpots(0, 50).subscribe({
      next: (page) => {
        this.touristSpots.set(page.content);
      },
      error: (err) => {
        console.error('Erro ao carregar pontos para o dropdown:', err);
      }
    });
  }

  getPhotoUrl(url: string | undefined): string {
    if (!url) return '';
    return url.startsWith('http') ? url : `${this.mediaUrl}${url}`;
  }
}
