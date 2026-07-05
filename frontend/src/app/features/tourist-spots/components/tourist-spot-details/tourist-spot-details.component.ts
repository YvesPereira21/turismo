import { Component, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { TouristSpotService } from '../../services/tourist-spot.service';
import { ActivatedRoute } from '@angular/router';
import { TouristSpot } from '../../../../core/models/tourist-spot';
import { SocialMediaListComponent } from '../../../social-medias/componentes/social-media-list/social-media-list.component';

@Component({
  selector: 'app-tourist-spot-details',
  imports: [SocialMediaListComponent],
  templateUrl: './tourist-spot-details.component.html',
  styleUrl: './tourist-spot-details.component.css'
})
export class TouristSpotDetailsComponent {
  public mediaUrl = environment.mediaUrl;

  private touristSpotService = inject(TouristSpotService)
  private activatedRoute = inject(ActivatedRoute)

  touristSpot = signal<TouristSpot | null>(null);
  errorMessage = '';

  ngOnInit(): void {
    const touristSpotId = this.activatedRoute.snapshot.paramMap.get('id');
    if (touristSpotId) {
      this.loadTouristSpot(touristSpotId);
    }
  }

  loadTouristSpot(touristSpotId: string) {
    this.touristSpotService.getTouristSpot(touristSpotId).subscribe({
      next: (response) => {
        this.touristSpot.set(response);
      },
      error: (error) => {
        this.errorMessage = 'Erro ao carregar a postagem.';
        alert(this.errorMessage);
      }
    })
  }
}
