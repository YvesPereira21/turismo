import { Routes } from '@angular/router';
import { TourGuideCreateComponent } from './features/tour-guides/components/tour-guide-create/tour-guide-create.component';
import { HomeComponent } from './features/home/home.component';
import { TouristCreateComponent } from './features/tourists/components/tourist-create/tourist-create.component';
import { SpotManagerCreateComponent } from './features/spot-managers/components/spot-manager-create/spot-manager-create.component';
import { TouristSpotCreateComponent } from './features/tourist-spots/components/tourist-spot-create/tourist-spot-create.component';
import { TouristSpotDetailsComponent } from './features/tourist-spots/components/tourist-spot-details/tourist-spot-details.component';
import { MapViewComponent } from './features/map/components/map-view/map-view.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'mapa', component: MapViewComponent },
  { path: 'create-tour-guide', component: TourGuideCreateComponent },
  { path: 'create-tourist', component: TouristCreateComponent },
  { path: 'create-spot-manager', component: SpotManagerCreateComponent },
  { path: 'create-tourist-spot', component: TouristSpotCreateComponent },
  { path: 'tourist-spots/:id', component: TouristSpotDetailsComponent }
];
