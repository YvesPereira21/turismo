import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  template: `
    <div class="h-screen flex flex-col items-center justify-center font-poppins gap-3 bg-gray-50">
      <span class="loading loading-spinner loading-lg text-[#2C5C3E]"></span>
      <p class="text-gray-600 text-sm font-medium">Autenticando com o Google, aguarde...</p>
    </div>
  `
})
export class AuthCallbackComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (token) {
      this.authService.saveToken(token).subscribe({
        next: () => this.router.navigate(['/']),
        error: () => this.router.navigate(['/'])
      });
    } else {
      alert('Não foi possível concluir a autenticação com o Google.');
      this.router.navigate(['/']);
    }
  }
}
