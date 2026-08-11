import { Component, inject, OnInit, output, signal } from '@angular/core';

import { TagService } from '../../../features/tags/services/tag.service';
import { CityService } from '../../../features/cities/services/city.service';
import { StateService } from '../../../features/states/services/state.service';

import { City } from '../../../core/models/city';
import { Tag } from '../../../core/models/tag';
import { State } from '../../../core/models/state';
import { TouristSpotFilters } from '../../../core/models/tourist-spot';

@Component({
  selector: 'app-filter-bar',
  imports: [],
  templateUrl: './filter-bar.component.html',
  styleUrl: './filter-bar.component.css'
})
export class FilterBarComponent implements OnInit {
  private tagService = inject(TagService);
  private cityService = inject(CityService);
  private stateService = inject(StateService);

  filterChange = output<TouristSpotFilters>();

  tags = signal<Tag[]>([]);
  cities = signal<City[]>([]);
  states = signal<State[]>([]);

  selectedState = signal<string | null>(null);
  selectedCity = signal<string | null>(null);
  selectedTags = signal<Set<string>>(new Set());
  selectedDistance = signal<number | null>(null);

  distanceOptions = [
    { label: '2km', value: 2 },
    { label: '5km', value: 5 },
    { label: '10km', value: 10 },
    { label: '20km', value: 20 }
  ];

  ngOnInit(): void {
    this.loadTags();
    this.loadStates();
  }

  loadTags() {
    this.tagService
      .getAllTags()
      .subscribe({
        next: (response) => {
          this.tags.set(response.content);
        },
        error: (error) => {
          console.error('Não foi possível carregar as tags', error);
        }
      });
  }

  loadStates() {
    this.stateService
      .getAllStates()
      .subscribe({
        next: (response) => {
          this.states.set(response);
        },
        error: (error) => {
          console.error('Não foi possível carregar os estados', error);
        }
      });
  }

  loadCities(stateName: string) {
    this.cityService
      .getCitiesFromState(stateName)
      .subscribe({
        next: (response) => {
          this.cities.set(response);
        },
        error: (error) => {
          console.error('Não foi possível carregar as cidades', error);
        }
      });
  }

  private emitFilters() {
    this.filterChange.emit({
      stateName: this.selectedState(),
      cityName: this.selectedCity(),
      tags: Array.from(this.selectedTags()),
      distance: this.selectedDistance()
    });
  }

  onStateChange(event: Event) {
    const select = event.target as HTMLSelectElement;
    const stateName = select.value;

    if (stateName) {
      this.selectedState.set(stateName);
      this.selectedCity.set(null);
      this.loadCities(stateName);
    } else {
      this.selectedState.set(null);
      this.cities.set([]);
    }

    this.emitFilters();
  }

  onCitySelect(cityName: string) {
    const current = this.selectedCity() === cityName ? null : cityName;
    this.selectedCity.set(current);
    this.emitFilters();
  }

  onTagToggle(tagName: string) {
    const tagsSelected = new Set(this.selectedTags());

    if (tagsSelected.has(tagName)) {
      tagsSelected.delete(tagName);
    } else {
      tagsSelected.add(tagName);
    }

    this.selectedTags.set(tagsSelected);
    this.emitFilters();
  }

  onDistanceChange(value: number) {
    if (this.selectedDistance() === value) {
      this.selectedDistance.set(null);
    } else {
      this.selectedDistance.set(value);
    }

    this.emitFilters();
  }
}
