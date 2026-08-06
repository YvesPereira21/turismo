import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { TouristSpotService } from '../../../tourist-spots/services/tourist-spot.service';
import { TourGuideService } from '../../../tour-guides/services/tour-guide.service';
import { AuthService } from '../../../../core/services/auth.service';
import { TouristSpotList } from '../../../../core/models/tourist-spot';
import { TourGuide } from '../../../../core/models/tour-guide';

@Component({
  selector: 'app-spot-manager-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './spot-manager-dashboard.component.html',
  styleUrl: './spot-manager-dashboard.component.css'
})
export class SpotManagerDashboardComponent implements OnInit {
  private touristSpotService = inject(TouristSpotService);
  private tourGuideService = inject(TourGuideService);
  public authService = inject(AuthService);
  private router = inject(Router);

  touristSpots = signal<TouristSpotList[]>([]);
  tourGuides = signal<TourGuide[]>([]);
  isLoading = signal(true);
  selectedSpotName = signal('');
  spotManagerId: string | undefined;

  ngOnInit(): void {
    this.spotManagerId = this.authService.currentUser()?.spotManagerId;
    if (this.spotManagerId) {
      this.loadTouristSpots();
    } else {
      this.isLoading.set(false);
    }
  }

  loadTouristSpots() {
    this.isLoading.set(true);
    this.touristSpotService.getSpotManagerTouristSpots(this.spotManagerId!, 0, 100).subscribe({
      next: (page) => {
        this.touristSpots.set(page.content);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Erro ao carregar pontos turísticos', err);
        this.isLoading.set(false);
      }
    });
  }

  openGuidesModal(spot: TouristSpotList) {
    this.selectedSpotName.set(spot.name);
    this.tourGuides.set([]); // clear previous
    this.tourGuideService.getTourGuidesByTouristSpot(spot.touristSpotId, 0, 50).subscribe({
      next: (page) => {
        this.tourGuides.set(page.content);
        const modal = document.getElementById('modal_guias') as HTMLDialogElement;
        if (modal) modal.showModal();
      },
      error: (err) => console.error('Erro ao carregar guias', err)
    });
  }

  closeGuidesModal() {
    const modal = document.getElementById('modal_guias') as HTMLDialogElement;
    if (modal) modal.close();
  }

  editSpot(id: string) {
    // Navigate to edit page (if it existed, or we can use create page with ID later)
    // For now we just alert or route if we have a route. 
    // Usually edit is another component, I will route to it.
    this.router.navigate(['/tourist-spots/edit', id]); // Placeholder route for future
  }

  deleteSpot(id: string) {
    if (confirm('Tem certeza que deseja deletar este ponto turístico?')) {
      this.touristSpotService.deleteTouristSpot(id).subscribe({
        next: () => {
          this.loadTouristSpots(); // reload
        },
        error: (err) => console.error('Erro ao deletar', err)
      });
    }
  }
  
  viewSpotDetails(id: string) {
    this.router.navigate(['/manager/tourist-spot', id]);
  }
  
  logout() {
    this.authService.logout();
  }
}
