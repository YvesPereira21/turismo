import { Component, signal } from '@angular/core';
import { WelcomeSectionComponent } from '../../shared/components/welcome-section/welcome-section.component';
import { FilterBarComponent } from '../../shared/components/filter-bar/filter-bar.component';
import { TouristSpotListComponent } from '../tourist-spots/components/tourist-spot-list/tourist-spot-list.component';

@Component({
  selector: 'app-home',
  imports: [
    WelcomeSectionComponent,
    FilterBarComponent,
    TouristSpotListComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  selectedDistance = signal<number | null>(null);

  onDistanceSelected(distance: number | null) {
    this.selectedDistance.set(distance);
  }
}
