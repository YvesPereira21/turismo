import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { TourGuideService } from '../../services/tour-guide.service';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { TourGuideCreate } from '../../../../core/models/tour-guide';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-tour-guide-create',
  imports: [ReactiveFormsModule],
  templateUrl: './tour-guide-create.component.html',
  styleUrl: './tour-guide-create.component.css'
})
export class TourGuideCreateComponent {
  private tourGuideService = inject(TourGuideService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private formBuilder = inject(FormBuilder);

  isSubmiting: boolean = false;
  tourGuideForm = this.formBuilder.group({
    cadastur: ['', { nonNullable: true, validators: [Validators.required] }],
    type: ['', { nonNullable: true, validators: [Validators.required] }],
    name: ['', { nonNullable: true, validators: [Validators.required] }],
    email: ['', { nonNullable: true, validators: [Validators.required, Validators.email] }],
    password: ['', { nonNullable: true, validators: [Validators.required] }],
    phone: ['']
  })

  onSubmit() {
    if (this.tourGuideForm.invalid) return alert('Por gentileza, insira as informações corretamente');
    this.isSubmiting = true;

    const formValues = this.tourGuideForm.value;
    const tourGuide: TourGuideCreate = {
      cadastur: formValues.cadastur!,
      type: formValues.type!,
      user: {
        name: formValues.name!,
        email: formValues.email!,
        password: formValues.password!,
        phone: formValues.phone!
      }
    }

    this.tourGuideService.createTourGuide(tourGuide).subscribe({
      next: () => {
        this.authService.login({ email: formValues.email, password: formValues.password }).subscribe({
          next: () => {
            this.router.navigate(['/']);
          },
          error: () => {
            alert('Conta de Guia criada com sucesso! Por favor, faça login para continuar.');
            this.router.navigate(['/login']);
          }
        });
      },
      error: () => {
        alert('Erro ao criar conta de Guia. Por favor, verifique se o e-mail ou CADASTUR já estão em uso.');
        this.isSubmiting = false;
      }
    })
  }

  clearForm() {
    this.tourGuideForm.reset();
    this.isSubmiting = false;
  }
}
