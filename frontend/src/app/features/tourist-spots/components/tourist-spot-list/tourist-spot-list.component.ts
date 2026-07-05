import { Component, inject, OnInit, signal } from '@angular/core';
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

  touristSpots = signal<TouristSpotList[]>([]);

  ngOnInit(): void {
    this.loadTouristSpots();
  }

  loadTouristSpots() {
    this.touristSpotService.getTouristSpots().subscribe({
      next: (response) => {
        this.touristSpots.set(response.content);
      },
      error: (error) => {
        alert("Não foi possível encontrar todos os pontos");
      }
    })
  }
}
