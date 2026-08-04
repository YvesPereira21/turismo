import { Routes } from '@angular/router';
import { TourGuideCreateComponent } from './features/tour-guides/components/tour-guide-create/tour-guide-create.component';
import { HomeComponent } from './features/home/home.component';
import { TouristCreateComponent } from './features/tourists/components/tourist-create/tourist-create.component';
import { SpotManagerCreateComponent } from './features/spot-managers/components/spot-manager-create/spot-manager-create.component';
import { TouristSpotCreateComponent } from './features/tourist-spots/components/tourist-spot-create/tourist-spot-create.component';
import { TouristSpotDetailsComponent } from './features/tourist-spots/components/tourist-spot-details/tourist-spot-details.component';
import { MapViewComponent } from './features/map/components/map-view/map-view.component';
import { AuthCallbackComponent } from './features/auth/components/auth-callback/auth-callback.component';
import { LoginComponent } from './features/auth/components/login/login.component';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent, canActivate: [authGuard] },
  { path: 'mapa', component: MapViewComponent, canActivate: [authGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'auth/callback', component: AuthCallbackComponent },
  { path: 'create-tour-guide', component: TourGuideCreateComponent },
  { path: 'create-tourist', component: TouristCreateComponent },
  {
    path: 'create-spot-manager',
    component: SpotManagerCreateComponent,
    canActivate: [roleGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: 'create-tourist-spot',
    component: TouristSpotCreateComponent,
    canActivate: [roleGuard],
    data: { roles: ['SPOTMANAGER'] }
  },
  { path: 'tourist-spots/:id', component: TouristSpotDetailsComponent, canActivate: [authGuard] }
];
