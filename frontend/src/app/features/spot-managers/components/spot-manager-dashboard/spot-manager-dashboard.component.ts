import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TouristSpotService } from '../../../tourist-spots/services/tourist-spot.service';
import { TourGuideService } from '../../../tour-guides/services/tour-guide.service';
import { WarnService } from '../../../warns/services/warn.service';
import { AuthService } from '../../../../core/services/auth.service';
import { TouristSpotList } from '../../../../core/models/tourist-spot';
import { TourGuide } from '../../../../core/models/tour-guide';
import { Warn } from '../../../../core/models/warn';
import { environment } from '../../../../environments/environment';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';

@Component({
  selector: 'app-spot-manager-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, PaginationComponent],
  templateUrl: './spot-manager-dashboard.component.html',
  styleUrl: './spot-manager-dashboard.component.css'
})
export class SpotManagerDashboardComponent implements OnInit {
  private touristSpotService = inject(TouristSpotService);
  private tourGuideService = inject(TourGuideService);
  private warnService = inject(WarnService);
  public authService = inject(AuthService);
  private router = inject(Router);
  public mediaUrl = environment.mediaUrl;

  touristSpots = signal<TouristSpotList[]>([]);
  tourGuides = signal<TourGuide[]>([]);
  warns = signal<Warn[]>([]);
  isLoading = signal(true);
  selectedSpotName = signal('');
  selectedSpotId = signal<string | null>(null);
  spotManagerId: string | undefined;

  // Pagination signals
  isEmpty = signal<boolean>(false);
  isFirst = signal<boolean>(true);
  isLast = signal<boolean>(false);
  pageSize = signal<number>(10);
  currentPage = signal<number>(0);
  numberOfElements = signal<number>(0);
  totalElements = signal<number>(0);
  totalPages = signal<number>(0);

  // New warn fields
  newWarnTitle = signal('');
  newWarnDescription = signal('');
  isCreatingWarn = signal(false);

  ngOnInit(): void {
    this.spotManagerId = this.authService.currentUser()?.spotManagerId;
    if (this.spotManagerId) {
      this.loadTouristSpots();
    } else {
      this.isLoading.set(false);
    }
  }

  loadTouristSpots(page = 0, size = 10) {
    this.isLoading.set(true);
    this.touristSpotService.getSpotManagerTouristSpots(this.spotManagerId!, page, size).subscribe({
      next: (pageResponse) => {
        this.touristSpots.set(pageResponse.content);
        this.isEmpty.set(pageResponse.empty);
        this.isFirst.set(pageResponse.first);
        this.isLast.set(pageResponse.last);
        this.pageSize.set(pageResponse.size);
        this.currentPage.set(pageResponse.number);
        this.numberOfElements.set(pageResponse.numberOfElements);
        this.totalElements.set(pageResponse.totalElements);
        this.totalPages.set(pageResponse.totalPages);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Erro ao carregar pontos turísticos', err);
        this.touristSpots.set([]);
        this.isEmpty.set(true);
        this.isLoading.set(false);
      }
    });
  }

  onPageChange(page: number) {
    this.loadTouristSpots(page, this.pageSize() || 10);
  }

  openGuidesModal(spot: TouristSpotList) {
    this.selectedSpotName.set(spot.name);
    this.tourGuides.set([]);
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

  // Warnings Modal logic
  openWarnsModal(spot: TouristSpotList) {
    this.selectedSpotName.set(spot.name);
    this.selectedSpotId.set(spot.touristSpotId);
    this.newWarnTitle.set('');
    this.newWarnDescription.set('');
    this.loadWarns(spot.touristSpotId);

    const modal = document.getElementById('modal_avisos') as HTMLDialogElement;
    if (modal) modal.showModal();
  }

  loadWarns(spotId: string) {
    this.warnService.getAllTouristSpotWarn(spotId, 0, 50).subscribe({
      next: (page) => this.warns.set(page.content),
      error: (err) => console.error('Erro ao carregar avisos', err)
    });
  }

  closeWarnsModal() {
    const modal = document.getElementById('modal_avisos') as HTMLDialogElement;
    if (modal) modal.close();
  }

  createWarn() {
    const spotId = this.selectedSpotId();
    const title = this.newWarnTitle().trim();
    const desc = this.newWarnDescription().trim();

    if (!spotId || !title || !desc) {
      return alert('Preencha o título e a descrição do aviso.');
    }

    this.isCreatingWarn.set(true);
    this.warnService.createWarn(spotId, { name: title, description: desc }).subscribe({
      next: () => {
        this.newWarnTitle.set('');
        this.newWarnDescription.set('');
        this.isCreatingWarn.set(false);
        this.loadWarns(spotId);
      },
      error: (err) => {
        console.error('Erro ao criar aviso', err);
        alert('Erro ao criar aviso.');
        this.isCreatingWarn.set(false);
      }
    });
  }

  deleteWarn(warnId: string) {
    if (confirm('Tem certeza que deseja excluir este aviso?')) {
      this.warnService.deleteWarn(warnId).subscribe({
        next: () => {
          if (this.selectedSpotId()) {
            this.loadWarns(this.selectedSpotId()!);
          }
        },
        error: (err) => console.error('Erro ao deletar aviso', err)
      });
    }
  }

  editSpot(id: string) {
    this.router.navigate(['/tourist-spots/edit', id]);
  }

  deleteSpot(id: string) {
    if (confirm('Tem certeza que deseja deletar este ponto turístico?')) {
      this.touristSpotService.deleteTouristSpot(id).subscribe({
        next: () => {
          this.loadTouristSpots(this.currentPage(), this.pageSize() || 10);
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
