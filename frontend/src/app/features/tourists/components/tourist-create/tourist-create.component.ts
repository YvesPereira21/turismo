import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { TouristService } from '../../services/tourist.service';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TouristCreate } from '../../../../core/models/tourist';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-tourist-create',
  imports: [ReactiveFormsModule],
  templateUrl: './tourist-create.component.html',
  styleUrl: './tourist-create.component.css'
})
export class TouristCreateComponent {
  private touristService = inject(TouristService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private formBuilder = inject(FormBuilder);

  isSubmiting: boolean = false;
  touristForm = this.formBuilder.group({
    birthDate: ['', { nonNullable: true, validators: [Validators.required] }],
    name: ['', { nonNullable: true, validators: [Validators.required] }],
    email: ['', { nonNullable: true, validators: [Validators.required, Validators.email] }],
    password: ['', { nonNullable: true, validators: [Validators.required] }],
    phone: ['']
  })

  onSubmit() {
    if (this.touristForm.invalid) return alert('Por gentileza, insira as informações corretamente');
    this.isSubmiting = true;

    const formValues = this.touristForm.value;
    const tourist: TouristCreate = {
      birthDate: formValues.birthDate!,
      user: {
        name: formValues.name!,
        email: formValues.email!,
        password: formValues.password!,
        phone: formValues.phone!
      }
    }

    this.touristService.createTourist(tourist).subscribe({
      next: () => {
        this.authService.login({ email: formValues.email, password: formValues.password }).subscribe({
          next: () => {
            this.router.navigate(['/']);
          },
          error: () => {
            alert('Conta criada com sucesso! Por favor, faça login para continuar.');
            this.router.navigate(['/login']);
          }
        });
      },
      error: () => {
        alert('Erro ao criar conta. Por favor, tente novamente');
        this.isSubmiting = false;
      }
    })
  }

  clearForm() {
    this.touristForm.reset();
    this.isSubmiting = false;
  }
}
