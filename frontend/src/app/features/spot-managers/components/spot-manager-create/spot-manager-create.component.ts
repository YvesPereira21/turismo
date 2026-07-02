import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { SpotManagerService } from '../../services/spot-manager.service';
import { SpotManagerCreate } from '../../../../core/models/spot-manager';
import { SocialMediaCreate } from '../../../../core/models/social-media';

@Component({
  selector: 'app-spot-manager-create',
  imports: [ReactiveFormsModule],
  templateUrl: './spot-manager-create.component.html',
  styleUrl: './spot-manager-create.component.css'
})
export class SpotManagerCreateComponent {
  private spotManagerService = inject(SpotManagerService);
  private formBuilder = inject(FormBuilder);

  isSubmiting: boolean = false;

  spotManagerForm = this.formBuilder.group({
    managerType: ['', { nonNullable: true, validators: [Validators.required] }],
    name: ['', { nonNullable: true, validators: [Validators.required] }],
    email: ['', { nonNullable: true, validators: [Validators.required, Validators.email] }],
    password: ['', { nonNullable: true, validators: [Validators.required] }],
    phone: [''],
    instagram: [''],
    facebook: [''],
    x: ['']
  });

  onSubmit() {
    if (this.spotManagerForm.invalid) return alert('Por gentileza, insira as informações corretamente');
    this.isSubmiting = true;

    const formValues = this.spotManagerForm.value;

    const socialsMedia: SocialMediaCreate[] = [];
    if (formValues.instagram?.trim()) {
      socialsMedia.push({ socialMediaLink: formValues.instagram.trim(), socialMediaType: 'INSTAGRAM' });
    }
    if (formValues.facebook?.trim()) {
      socialsMedia.push({ socialMediaLink: formValues.facebook.trim(), socialMediaType: 'FACEBOOK' });
    }
    if (formValues.x?.trim()) {
      socialsMedia.push({ socialMediaLink: formValues.x.trim(), socialMediaType: 'X' });
    }

    const spotManager: SpotManagerCreate = {
      managerType: formValues.managerType!,
      user: {
        name: formValues.name!,
        email: formValues.email!,
        password: formValues.password!,
        phone: formValues.phone!
      },
      socialsMedia: socialsMedia
    };

    this.spotManagerService.createSpotManager(spotManager).subscribe({
      next: (response) => {
        alert('Conta de gestor criada com sucesso!');
        this.clearForm();
      },
      error: (erro) => {
        alert('Erro ao criar conta de gestor. Por favor, tente novamente');
        this.isSubmiting = false;
      }
    });
  }

  clearForm() {
    this.spotManagerForm.reset();
    this.isSubmiting = false;
  }
}
