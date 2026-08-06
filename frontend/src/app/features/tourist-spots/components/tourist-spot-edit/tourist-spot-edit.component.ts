import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PhotoUpload } from '../../../../core/models/photo';
import { PhotoService } from '../../../photos/services/photo.service';
import { TagSelectorComponent } from '../../../tags/components/tag-selector/tag-selector.component';
import { TouristSpot, TouristSpotUpdate } from '../../../../core/models/tourist-spot';
import { TouristSpotService } from '../../services/tourist-spot.service';
import { StateService } from '../../../states/services/state.service';
import { CityService } from '../../../cities/services/city.service';
import { State } from '../../../../core/models/state';
import { City } from '../../../../core/models/city';

@Component({
  selector: 'app-tourist-spot-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TagSelectorComponent, RouterLink],
  templateUrl: './tourist-spot-edit.component.html',
  styleUrl: './tourist-spot-edit.component.css'
})
export class TouristSpotEditComponent implements OnInit {
  private touristSpotService = inject(TouristSpotService);
  private photoService = inject(PhotoService);
  private formBuilder = inject(FormBuilder);
  private stateService = inject(StateService);
  private cityService = inject(CityService);
  private activatedRoute = inject(ActivatedRoute);
  private router = inject(Router);

  touristSpotId: string | null = null;
  touristSpot = signal<TouristSpot | null>(null);
  photos = signal<PhotoUpload[]>([]);
  isSubmiting = signal<boolean>(false);
  isLoading = signal<boolean>(true);
  states = signal<State[]>([]);
  cities = signal<City[]>([]);

  touristSpotForm = this.formBuilder.group({
    name: ['', { nonNullable: true, validators: [Validators.required] }],
    opensAt: ['', { nonNullable: true, validators: [Validators.required] }],
    closesAt: ['', { nonNullable: true, validators: [Validators.required] }],
    shortDescription: ['', { nonNullable: true, validators: [Validators.required] }],
    description: ['', { nonNullable: true, validators: [Validators.required] }],
    cityName: ['', { nonNullable: true, validators: [Validators.required] }],
    stateName: [''],
    tags: this.formBuilder.array([]),
    instagram: [''],
    facebook: [''],
    x: ['']
  });

  get tagsArray(): FormArray {
    return this.touristSpotForm.get('tags') as FormArray;
  }

  ngOnInit() {
    this.touristSpotId = this.activatedRoute.snapshot.paramMap.get('id');
    this.loadStates();

    if (this.touristSpotId) {
      this.loadTouristSpot(this.touristSpotId);
    } else {
      this.isLoading.set(false);
    }
  }

  loadStates() {
    this.stateService.getAllStates().subscribe({
      next: (data) => this.states.set(data),
      error: (err) => console.error('Erro ao carregar estados', err)
    });
  }

  loadTouristSpot(id: string) {
    this.isLoading.set(true);
    this.touristSpotService.getTouristSpot(id).subscribe({
      next: (spot) => {
        this.touristSpot.set(spot);

        // Pre-fill socials
        let insta = '';
        let fb = '';
        let x = '';
        if (spot.socialsMedia) {
          spot.socialsMedia.forEach(sm => {
            if (sm.socialMediaType === 'INSTAGRAM') insta = sm.socialMediaLink;
            if (sm.socialMediaType === 'FACEBOOK') fb = sm.socialMediaLink;
            if (sm.socialMediaType === 'X') x = sm.socialMediaLink;
          });
        }

        // Fill form
        this.touristSpotForm.patchValue({
          name: spot.name,
          opensAt: spot.opensAt,
          closesAt: spot.closesAt,
          shortDescription: spot.shortDescription,
          description: spot.description,
          cityName: spot.city?.name || '',
          stateName: spot.city?.stateName || '',
          instagram: insta,
          facebook: fb,
          x: x
        });

        // Fill tags
        if (spot.tags) {
          spot.tags.forEach(tag => {
            this.tagsArray.push(this.formBuilder.control(tag.name));
          });
        }

        // Fill cities for state if state exists
        if (spot.city?.stateName) {
          this.cityService.getCitiesFromState(spot.city.stateName, 0, 1000).subscribe({
            next: (page) => this.cities.set(page.content),
            error: (err) => console.error(err)
          });
        }

        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Erro ao carregar ponto turístico', err);
        this.isLoading.set(false);
      }
    });
  }

  onStateChange(event: Event) {
    const selectElement = event.target as HTMLSelectElement;
    const stateName = selectElement.value;
    if (stateName) {
      this.cityService.getCitiesFromState(stateName, 0, 1000).subscribe({
        next: (page) => {
          this.cities.set(page.content);
          this.touristSpotForm.patchValue({ cityName: '' });
        },
        error: (err) => console.error('Erro ao carregar cidades', err)
      });
    } else {
      this.cities.set([]);
      this.touristSpotForm.patchValue({ cityName: '' });
    }
  }

  async onSubmit() {
    if (this.touristSpotForm.invalid || !this.touristSpotId) {
      return alert('Por favor, preencha os campos obrigatórios.');
    }

    this.isSubmiting.set(true);

    const formValues = this.touristSpotForm.value;
    const updateDTO: TouristSpotUpdate = {
      name: formValues.name || undefined,
      opensAt: formValues.opensAt || undefined,
      closesAt: formValues.closesAt || undefined,
      shortDescription: formValues.shortDescription || undefined,
      description: formValues.description || undefined,
      cityName: formValues.cityName || undefined,
      tags: (formValues.tags as string[]) || [],
      socialsMedia: []
    };

    if (formValues.instagram?.trim()) {
      updateDTO.socialsMedia!.push({ socialMediaLink: formValues.instagram.trim(), socialMediaType: 'INSTAGRAM' });
    }
    if (formValues.facebook?.trim()) {
      updateDTO.socialsMedia!.push({ socialMediaLink: formValues.facebook.trim(), socialMediaType: 'FACEBOOK' });
    }
    if (formValues.x?.trim()) {
      updateDTO.socialsMedia!.push({ socialMediaLink: formValues.x.trim(), socialMediaType: 'X' });
    }

    this.touristSpotService.updateTouristSpot(this.touristSpotId, updateDTO).subscribe({
      next: () => {
        if (this.photos().length > 0) {
          this.uploadPhotos(this.touristSpotId!);
        } else {
          this.isSubmiting.set(false);
          this.router.navigate(['/manager-dashboard']);
        }
      },
      error: (err) => {
        console.error('Erro ao atualizar ponto turístico', err);
        alert('Erro ao atualizar o ponto turístico.');
        this.isSubmiting.set(false);
      }
    });
  }

  onFilesSelected(event: Event) {
    const inputElement = event.target as HTMLInputElement;

    if (inputElement.files && inputElement.files.length > 0) {
      if (inputElement.files.length > 5) {
        alert('Você só deve selecionar no máximo 5 fotos de cada vez.');
        inputElement.value = '';
        return;
      }

      const selectedPhotos = Array.from(inputElement.files).map(file => ({
        photo: file,
        altText: ''
      }));
      this.photos.set(selectedPhotos);
    }
  }

  updateAlternateTextInForm(index: number, event: Event) {
    const inputElement = event.target as HTMLInputElement;
    this.photos.update(prev => {
      const updated = [...prev];
      updated[index] = { ...updated[index], altText: inputElement.value };
      return updated;
    });
  }

  uploadPhotos(touristSpotId: string) {
    const photosList = this.photos();
    let completed = 0;

    for (let i = 0; i < photosList.length; i++) {
      const currentPhoto = photosList[i];
      this.photoService.uploadTouristSpotPhotos(currentPhoto, touristSpotId).subscribe({
        next: () => {
          completed++;
          if (completed === photosList.length) {
            this.isSubmiting.set(false);
            this.router.navigate(['/manager-dashboard']);
          }
        },
        error: (err) => {
          console.error(`Erro na foto ${currentPhoto.photo.name}:`, err);
          completed++;
          if (completed === photosList.length) {
            this.isSubmiting.set(false);
            this.router.navigate(['/manager-dashboard']);
          }
        }
      });
    }
  }
}
