import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const homeGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    const currentRole = authService.userRole();
    if (currentRole === 'SPOTMANAGER') {
      return router.parseUrl('/manager-dashboard');
    }
    return true;
  }
  
  return true;
};
