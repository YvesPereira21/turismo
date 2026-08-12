import { Component, inject, OnInit, signal, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TouristSpotService } from '../../../tourist-spots/services/tourist-spot.service';
import { TouristSpot } from '../../../../core/models/tourist-spot';
import { SocialMediaListComponent } from '../../../social-medias/componentes/social-media-list/social-media-list.component';
import { ActivityListComponent } from '../../../activities/components/activity-list/activity-list.component';
import { ActivityCreateComponent } from '../../../activities/components/activity-create/activity-create.component';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-manager-tourist-spot-details',
  standalone: true,
  imports: [CommonModule, RouterLink, SocialMediaListComponent, ActivityListComponent, ActivityCreateComponent],
  templateUrl: './manager-tourist-spot-details.component.html',
  styleUrl: './manager-tourist-spot-details.component.css'
})
export class ManagerTouristSpotDetailsComponent implements OnInit {
  private touristSpotService = inject(TouristSpotService);
  private activatedRoute = inject(ActivatedRoute);

  public mediaUrl = environment.mediaUrl;

  touristSpot = signal<TouristSpot | null>(null);
  isLoading = signal(true);
  errorMessage = signal('');

  ngOnInit(): void {
    const touristSpotId = this.activatedRoute.snapshot.paramMap.get('id');
    if (touristSpotId) {
      this.loadTouristSpot(touristSpotId);
    } else {
      this.errorMessage.set('ID do ponto turístico não encontrado.');
      this.isLoading.set(false);
    }
  }

  loadTouristSpot(touristSpotId: string) {
    this.isLoading.set(true);
    this.touristSpotService.getTouristSpot(touristSpotId).subscribe({
      next: (response) => {
        this.touristSpot.set(response);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error(error);
        this.errorMessage.set('Erro ao carregar o ponto turístico.');
        this.isLoading.set(false);
      }
    });
  }

  @ViewChild(ActivityListComponent) activityList!: ActivityListComponent;

  onActivityCreated() {
    const spot = this.touristSpot();
    if (spot) {
      this.activityList.loadActivities(spot.touristSpotId);
    }
  }
}
