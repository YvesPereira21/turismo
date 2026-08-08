import { Component, inject, OnInit, signal } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PhotoUpload } from '../../../../core/models/photo';
import { PhotoService } from '../../../photos/services/photo.service';
import { TagSelectorComponent } from '../../../tags/components/tag-selector/tag-selector.component';
import { TouristSpotCreate } from '../../../../core/models/tourist-spot';
import { SocialMediaCreate } from '../../../../core/models/social-media';
import { TouristSpotService } from '../../services/tourist-spot.service';
import { StateService } from '../../../states/services/state.service';
import { CityService } from '../../../cities/services/city.service';
import { State } from '../../../../core/models/state';
import { City } from '../../../../core/models/city';

import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-tourist-spot-create',
  imports: [ReactiveFormsModule, TagSelectorComponent, RouterLink],
  templateUrl: './tourist-spot-create.component.html',
  styleUrl: './tourist-spot-create.component.css'
})
export class TouristSpotCreateComponent implements OnInit {
  private touristSpotService = inject(TouristSpotService);
  private photoService = inject(PhotoService);
  private formBuilder = inject(FormBuilder);
  private stateService = inject(StateService);
  private cityService = inject(CityService);
  private authService = inject(AuthService);
  private router = inject(Router);

  photos = signal<PhotoUpload[]>([]);
  isSubmiting = signal<boolean>(false);
  states = signal<State[]>([]);
  cities = signal<City[]>([]);

  touristSpotForm = this.formBuilder.group({
    name: ['', { nonNullable: true, validators: [Validators.required] }],
    opensAt: ['', { nonNullable: true, validators: [Validators.required] }],
    closesAt: ['', { nonNullable: true, validators: [Validators.required] }],
    shortDescription: ['', { nonNullable: true, validators: [Validators.required] }],
    description: ['', { nonNullable: true, validators: [Validators.required] }],
    cityId: ['', { nonNullable: true, validators: [Validators.required] }],
    tags: this.formBuilder.array([]),
    instagram: [''],
    facebook: [''],
    x: ['']
  });

  get tagsArray(): FormArray {
    return this.touristSpotForm.get('tags') as FormArray;
  };

  ngOnInit() {
    this.loadStates();
  }

  loadStates() {
    this.stateService.getAllStates().subscribe({
      next: (data) => this.states.set(data),
      error: (err) => console.error('Erro ao carregar estados', err)
    });
  }

  onStateChange(event: Event) {
    const selectElement = event.target as HTMLSelectElement;
    const stateName = selectElement.value;
    if (stateName) {
      this.cityService.getCitiesFromState(stateName, 0, 1000).subscribe({
        next: (page) => {
          this.cities.set(page.content);
          this.touristSpotForm.patchValue({ cityId: '' });
        },
        error: (err) => console.error('Erro ao carregar cidades', err)
      });
    } else {
      this.cities.set([]);
      this.touristSpotForm.patchValue({ cityId: '' });
    }
  }

  async onSubmit() {
    try {
      if (this.touristSpotForm.invalid) return alert('Informações erradas')
      this.isSubmiting.set(true);

      const coordinates = await this.getLocation();

      const formValues = this.touristSpotForm.value;
      const touristSpot: TouristSpotCreate = {
        name: formValues.name!,
        longitude: coordinates.longitude,
        latitude: coordinates.latitude,
        opensAt: formValues.opensAt!,
        closesAt: formValues.closesAt!,
        shortDescription: formValues.shortDescription!,
        description: formValues.description!,
        cityId: formValues.cityId!,
        tags: formValues.tags as string[],
        socialsMedia: []
      }

      if (formValues.instagram?.trim()) {
        touristSpot.socialsMedia!.push({ socialMediaLink: formValues.instagram.trim(), socialMediaType: 'INSTAGRAM' });
      }
      if (formValues.facebook?.trim()) {
        touristSpot.socialsMedia!.push({ socialMediaLink: formValues.facebook.trim(), socialMediaType: 'FACEBOOK' });
      }
      if (formValues.x?.trim()) {
        touristSpot.socialsMedia!.push({ socialMediaLink: formValues.x.trim(), socialMediaType: 'X' });
      }

      this.touristSpotService.createTouristSpot(touristSpot).subscribe({
        next: (response) => {
          const touristSpotId = response.touristSpotId;
          if (this.photos().length > 0) {
            this.uploadPhotos(touristSpotId);
          } else {
            this.isSubmiting.set(false);
            this.router.navigate(['/manager-dashboard']);
          }
        },
        error: (err) => {
          if (err.error?.errors) {
            alert(err.error.errors[0]);
          } else {
            alert('Erro ao criar o ponto turístico.');
          }
          this.isSubmiting.set(false);
        }
      })

    }
    catch (error) {
      alert('Você precisa permitir o GPS para cadastrar o ponto turístico no mapa!')
      this.isSubmiting.set(false);
    }
  }

  getLocation(): Promise<{ latitude: number, longitude: number }> {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject('Geolocalização deve ser permitida.')
        return
      }

      navigator.geolocation.getCurrentPosition(
        (position) => {
          resolve({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude
          },
          );
        },
        (error) => {
          reject('Não foi possível obter a localização.')
        }
      );
    });
  }

  onFilesSelected(event: Event) {
    const inputElement = event.target as HTMLInputElement;

    if (inputElement.files && inputElement.files.length > 0) {
      if (inputElement.files.length > 5) {
        alert('Você só deve selecionar 5 fotos')
        inputElement.value = '';
        return
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
      const currentPhoto = photosList[i]

      this.photoService.uploadTouristSpotPhotos(currentPhoto, touristSpotId)
        .subscribe({
          next: (response) => {
            console.log(`✅ Foto ${currentPhoto.photo.name} enviada!`, response);
            completed++;
            if (completed === photosList.length) {
              this.isSubmiting.set(false);
              this.router.navigate(['/manager-dashboard']);
            }
          },
          error: (error) => {
            console.error(`❌ Erro na foto ${currentPhoto.photo.name}:`, error);
            completed++;
            if (completed === photosList.length) {
              this.isSubmiting.set(false);
              this.router.navigate(['/manager-dashboard']);
            }
          }
        });
    }
  }

  clearForm() {
    this.touristSpotForm.reset();
    (this.touristSpotForm.get('tags') as FormArray).clear();
    this.photos.set([]);
    this.cities.set([]);
  }
}
