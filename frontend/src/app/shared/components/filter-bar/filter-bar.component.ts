import { Component, inject, OnInit, signal } from '@angular/core';
import { TagService } from '../../../features/tags/services/tag.service';
import { CityService } from '../../../features/cities/services/city.service';
import { StateService } from '../../../features/states/services/state.service';
import { City } from '../../../core/models/city';
import { Tag } from '../../../core/models/tag';
import { State } from '../../../core/models/state';

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

  tags = signal<Tag[]>([]);
  cities = signal<City[]>([]);
  states = signal<State[]>([]);
  selectedState = signal<string | null>(null);

  ngOnInit(): void {
    this.loadTags();
    this.loadStates();
  }

  loadTags() {
    this.tagService.getAllTags().subscribe({
      next: (response) => {
        this.tags.set(response.content)
      },
      error: (error) => {
        console.error("Não foi possível carregar as tags", error)
      }
    })
  }

  loadStates() {
    this.stateService.getAllStates().subscribe({
      next: (response) => {
        this.states.set(response);
      },
      error: (error) => {
        console.error("Não foi possível carregar os estados", error)
      }
    })
  }

  onStateChange(event: Event) {
    const select = event.target as HTMLSelectElement;
    const stateName = select.value;

    if (stateName) {
      this.selectedState.set(stateName);
      this.loadCities(stateName);
    } else {
      this.selectedState.set(null);
      this.cities.set([]);
    }
  }

  loadCities(stateName: string) {
    this.cityService.getCitiesFromState(stateName).subscribe({
      next: (response) => {
        this.cities.set(response.content);
      },
      error: (error) => {
        console.error("Não foi possível carregar as cidades", error);
      }
    })
  }
}
