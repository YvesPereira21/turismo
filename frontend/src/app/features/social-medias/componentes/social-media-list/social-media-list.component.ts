import { Component, input } from '@angular/core';
import { SocialMedia } from '../../../../core/models/social-media';

@Component({
  selector: 'app-social-media-list',
  imports: [],
  templateUrl: './social-media-list.component.html',
  styleUrl: './social-media-list.component.css'
})
export class SocialMediaListComponent {
  socialsMedia = input<SocialMedia[]>([]);
}
