import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TouristSpotListComponent } from './tourist-spot-list.component';

describe('TouristSpotListComponent', () => {
  let component: TouristSpotListComponent;
  let fixture: ComponentFixture<TouristSpotListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TouristSpotListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TouristSpotListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
