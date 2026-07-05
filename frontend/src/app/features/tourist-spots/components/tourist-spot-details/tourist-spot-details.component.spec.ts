import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TouristSpotDetailsComponent } from './tourist-spot-details.component';

describe('TouristSpotDetailsComponent', () => {
  let component: TouristSpotDetailsComponent;
  let fixture: ComponentFixture<TouristSpotDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TouristSpotDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TouristSpotDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
