import { Component, input, effect, inject, signal, untracked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Activity } from '../../../../core/models/activity';
import { ActivityService } from '../../services/activity.service';
import { ActivityEditComponent } from '../activity-edit/activity-edit.component';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-activity-list',
  standalone: true,
  imports: [CommonModule, ActivityEditComponent],
  templateUrl: './activity-list.component.html',
  styleUrl: './activity-list.component.css'
})
export class ActivityListComponent {
  touristSpotId = input<string>();
  isManager = input<boolean>(false);
  activitiesInput = input<Activity[] | undefined>(undefined, { alias: 'activities' });

  public mediaUrl = environment.mediaUrl;
  private activityService = inject(ActivityService);

  activitiesList = signal<Activity[]>([]);
  isLoading = signal<boolean>(false);
  errorMessage = signal<string>('');

  editingActivity = signal<Activity | null>(null);
  deletingActivityId = signal<string | null>(null);

  private lastLoadedSpotId: string | null = null;

  constructor() {
    effect(() => {
      const val = this.activitiesInput();
      if (val !== undefined) {
        this.activitiesList.set(val);
        this.isLoading.set(false);
      }
    });

    effect(() => {
      const spotId = this.touristSpotId();
      const inputVal = untracked(() => this.activitiesInput());

      if (spotId && inputVal === undefined && spotId !== this.lastLoadedSpotId) {
        this.lastLoadedSpotId = spotId;
        this.loadActivities(spotId);
      }
    });
  }

  loadActivities(touristSpotId: string): void {
    this.isLoading.set(true);
    this.errorMessage.set('');
    this.activityService.getActivitiesByTouristSpotId(touristSpotId).subscribe({
      next: (data) => {
        this.activitiesList.set(data || []);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Erro ao carregar atividades', err);
        this.errorMessage.set('Não foi possível carregar as atividades.');
        this.isLoading.set(false);
      }
    });
  }

  openEditModal(activity: Activity): void {
    this.editingActivity.set(activity);
  }

  closeEditModal(): void {
    this.editingActivity.set(null);
  }

  onActivityUpdated(): void {
    this.closeEditModal();
    const spotId = this.touristSpotId();
    if (spotId) {
      this.loadActivities(spotId);
    }
  }

  confirmDelete(activity: Activity): void {
    if (confirm(`Tem certeza que deseja excluir a atividade "${activity.name}"?`)) {
      this.deletingActivityId.set(activity.activityId);
      this.activityService.deleteActivity(activity.activityId).subscribe({
        next: () => {
          this.deletingActivityId.set(null);
          this.activitiesList.update(list => list.filter(a => a.activityId !== activity.activityId));
        },
        error: (err) => {
          alert('Erro ao excluir a atividade.');
          this.deletingActivityId.set(null);
        }
      });
    }
  }
}
