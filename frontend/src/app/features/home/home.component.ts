import { Component } from '@angular/core';
import { WelcomeSectionComponent } from '../../shared/components/welcome-section/welcome-section.component';
import { FilterBarComponent } from '../../shared/components/filter-bar/filter-bar.component';

@Component({
  selector: 'app-home',
  imports: [
    WelcomeSectionComponent,
    FilterBarComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {}
