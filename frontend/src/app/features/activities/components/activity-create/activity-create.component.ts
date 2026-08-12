import { Component, input, output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivityService } from '../../services/activity.service';
import { PhotoService } from '../../../photos/services/photo.service';
import { PhotoUpload } from '../../../../core/models/photo';

@Component({
  selector: 'app-activity-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './activity-create.component.html',
  styleUrl: './activity-create.component.css'
})
export class ActivityCreateComponent {
  touristSpotId = input.required<string>();
  activityCreated = output<void>();

  private formBuilder = inject(FormBuilder);
  private activityService = inject(ActivityService);
  private photoService = inject(PhotoService);

  isSubmiting = signal<boolean>(false);
  selectedPhoto = signal<PhotoUpload | null>(null);

  activityForm = this.formBuilder.group({
    name: ['', { nonNullable: true, validators: [Validators.required] }]
  });

  onFileSelected(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    if (inputElement.files && inputElement.files.length > 0) {
      if (inputElement.files.length > 1) {
        alert('Você só deve selecionar 1 foto para a atividade.');
        inputElement.value = '';
        return;
      }
      this.selectedPhoto.set({
        photo: inputElement.files[0],
        altText: ''
      });
    } else {
      this.selectedPhoto.set(null);
    }
  }

  onSubmit() {
    if (this.activityForm.invalid) {
      alert('Preencha o nome da atividade.');
      return;
    }
    if (!this.selectedPhoto()) {
      alert('É obrigatório enviar uma foto para a atividade.');
      return;
    }

    this.isSubmiting.set(true);

    const name = this.activityForm.value.name!;
    
    this.activityService.createActivity(this.touristSpotId(), { name }).subscribe({
      next: (response) => {
        const activityId = response.activityId;
        this.photoService.uploadActivityPhoto(this.selectedPhoto()!, activityId).subscribe({
          next: () => {
            this.isSubmiting.set(false);
            this.activityForm.reset();
            this.selectedPhoto.set(null);
            
            this.activityCreated.emit();
          },
          error: (err) => {
            console.error('Erro ao fazer upload da foto:', err);
            alert('A atividade foi criada, mas ocorreu um erro ao enviar a foto.');
            this.isSubmiting.set(false);
            this.activityCreated.emit();
          }
        });
      },
      error: (err) => {
        console.error('Erro ao criar atividade:', err);
        alert('Erro ao criar a atividade.');
        this.isSubmiting.set(false);
      }
    });
  }
}
