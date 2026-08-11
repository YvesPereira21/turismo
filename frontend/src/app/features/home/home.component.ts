import { Component, computed, signal } from '@angular/core';

import { WelcomeSectionComponent } from '../../shared/components/welcome-section/welcome-section.component';
import { FilterBarComponent } from '../../shared/components/filter-bar/filter-bar.component';
import { TouristSpotListComponent } from '../tourist-spots/components/tourist-spot-list/tourist-spot-list.component';
import { TouristSpotFilters } from '../../core/models/tourist-spot';

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
  searchTerm = signal<string>('');
  sidebarFilters = signal<TouristSpotFilters>({});

  activeFilters = computed<TouristSpotFilters>(() => ({
    ...this.sidebarFilters(),
    name: this.searchTerm().trim() ? this.searchTerm().trim() : null
  }));

  onSearchChange(term: string) {
    this.searchTerm.set(term);
  }

  onFilterChange(filters: TouristSpotFilters) {
    this.sidebarFilters.set(filters);
  }
}
