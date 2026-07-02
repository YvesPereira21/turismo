import { Routes } from '@angular/router';
import { TourGuideCreateComponent } from './features/tour-guides/components/tour-guide-create/tour-guide-create.component';
import { HomeComponent } from './features/home/home.component';
import { TouristCreateComponent } from './features/tourists/components/tourist-create/tourist-create.component';
import { SpotManagerCreateComponent } from './features/spot-managers/components/spot-manager-create/spot-manager-create.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'create-tour-guide', component: TourGuideCreateComponent },
  { path: 'create-tourist', component: TouristCreateComponent },
  { path: 'create-spot-manager', component: SpotManagerCreateComponent }
];
