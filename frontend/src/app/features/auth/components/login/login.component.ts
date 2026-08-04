import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private formBuilder = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  isSubmitting = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  loginForm = this.formBuilder.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  onSubmit() {
    if (this.loginForm.invalid) {
      this.errorMessage.set('Por favor, preencha todos os campos corretamente.');
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    const credentials = this.loginForm.value;

    this.authService.login(credentials).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.router.navigate(['/']);
      },
      error: (error) => {
        this.isSubmitting.set(false);
        this.errorMessage.set(error.error?.message || 'E-mail ou senha incorretos.');
      }
    });
  }
}
