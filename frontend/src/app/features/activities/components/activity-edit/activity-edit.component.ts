import { Component, input, output, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Activity } from '../../../../core/models/activity';
import { ActivityService } from '../../services/activity.service';
import { PhotoService } from '../../../photos/services/photo.service';
import { PhotoUpload } from '../../../../core/models/photo';

@Component({
  selector: 'app-activity-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './activity-edit.component.html',
  styleUrl: './activity-edit.component.css'
})
export class ActivityEditComponent implements OnInit {
  activity = input.required<Activity>();
  closed = output<void>();
  activityUpdated = output<void>();

  private formBuilder = inject(FormBuilder);
  private activityService = inject(ActivityService);
  private photoService = inject(PhotoService);

  isSubmitting = signal<boolean>(false);
  selectedPhoto = signal<PhotoUpload | null>(null);

  editForm = this.formBuilder.group({
    name: ['', { nonNullable: true, validators: [Validators.required] }]
  });

  ngOnInit(): void {
    if (this.activity()) {
      this.editForm.patchValue({ name: this.activity().name });
    }
  }

  onFileSelected(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    if (inputElement.files && inputElement.files.length > 0) {
      this.selectedPhoto.set({
        photo: inputElement.files[0],
        altText: ''
      });
    } else {
      this.selectedPhoto.set(null);
    }
  }

  closeModal(): void {
    this.closed.emit();
  }

  onSubmit(): void {
    if (this.editForm.invalid) return;

    this.isSubmitting.set(true);
    const newName = this.editForm.value.name!;
    const activityId = this.activity().activityId;

    this.activityService.updateActivity(activityId, { name: newName }).subscribe({
      next: () => {
        const photo = this.selectedPhoto();
        if (photo) {
          this.photoService.updateActivityPhoto(photo, activityId).subscribe({
            next: () => {
              this.isSubmitting.set(false);
              this.activityUpdated.emit();
            },
            error: (err) => {
              alert('Erro ao atualizar a foto da atividade.');
              this.isSubmitting.set(false);
              this.activityUpdated.emit();
            }
          });
        } else {
          this.isSubmitting.set(false);
          this.activityUpdated.emit();
        }
      },
      error: (err) => {
        console.error('Erro ao atualizar atividade:', err);
        alert('Erro ao atualizar os dados da atividade.');
        this.isSubmitting.set(false);
      }
    });
  }
}
