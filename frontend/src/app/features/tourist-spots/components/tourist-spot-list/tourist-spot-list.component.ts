import { Component, effect, inject, input, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { environment } from '../../../../environments/environment';
import { TouristSpotService } from '../../services/tourist-spot.service';
import { TouristSpotFilters, TouristSpotList } from '../../../../core/models/tourist-spot';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';

@Component({
  selector: 'app-tourist-spot-list',
  standalone: true,
  imports: [RouterLink, PaginationComponent],
  templateUrl: './tourist-spot-list.component.html',
  styleUrl: './tourist-spot-list.component.css'
})
export class TouristSpotListComponent implements OnInit {
  private touristSpotService = inject(TouristSpotService);
  public mediaUrl = environment.mediaUrl;

  filters = input<TouristSpotFilters | null>(null);
  touristSpots = signal<TouristSpotList[]>([]);

  isEmpty = signal<boolean>(false);
  isFirst = signal<boolean>(true);
  isLast = signal<boolean>(false);
  pageSize = signal<number>(10);
  currentPage = signal<number>(0);
  numberOfElements = signal<number>(0);
  totalElements = signal<number>(0);
  totalPages = signal<number>(0);

  constructor() {
    effect(async () => {
      const filter = this.filters();

      let longitude: number | undefined;
      let latitude: number | undefined;

      if (filter?.distance) {
        try {
          const coordinate = await this.getLocation();
          longitude = coordinate.longitude;
          latitude = coordinate.latitude;
        } catch (error) {
          alert('Permita a localização para filtrar por distância');
          return;
        }
      }

      this.loadTouristSpots(
        filter?.name,
        filter?.cityName,
        filter?.stateName,
        filter?.tags,
        longitude,
        latitude,
        filter?.distance,
        0,
        this.pageSize() || 10
      );
    });
  }

  ngOnInit(): void { }

  loadTouristSpots(
    name?: string | null,
    cityName?: string | null,
    stateName?: string | null,
    tags?: string[] | null,
    longitude?: number | null,
    latitude?: number | null,
    radius?: number | null,
    page = 0,
    size = 10
  ) {
    this.touristSpotService
      .getTouristSpots({
        name,
        cityName,
        stateName,
        tags,
        longitude,
        latitude,
        radius
      }, page, size)
      .subscribe({
        next: (response) => {
          this.touristSpots.set(response.content);
          this.isEmpty.set(response.empty);
          this.isFirst.set(response.first);
          this.isLast.set(response.last);
          this.pageSize.set(response.size);
          this.currentPage.set(response.number);
          this.numberOfElements.set(response.numberOfElements);
          this.totalElements.set(response.totalElements);
          this.totalPages.set(response.totalPages);
        },
        error: (error) => {
          console.error('Não foi possível encontrar os pontos turísticos', error);
          this.touristSpots.set([]);
          this.isEmpty.set(true);
        }
      });
  }

  onPageChange(page: number) {
    const filter = this.filters();
    this.loadTouristSpots(
      filter?.name,
      filter?.cityName,
      filter?.stateName,
      filter?.tags,
      undefined,
      undefined,
      filter?.distance,
      page,
      this.pageSize() || 10
    );
  }

  getLocation(): Promise<{ latitude: number; longitude: number }> {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject('Geolocalização não suportada');
        return;
      }

      navigator.geolocation.getCurrentPosition(
        (position) => {
          resolve({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude
          });
        },
        (error) => {
          reject('Não foi possível obter a localização');
        }
      );
    });
  }
}
