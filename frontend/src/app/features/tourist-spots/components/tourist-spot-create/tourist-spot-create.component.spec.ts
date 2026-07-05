import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TouristSpotCreateComponent } from './tourist-spot-create.component';

describe('TouristSpotCreateComponent', () => {
  let component: TouristSpotCreateComponent;
  let fixture: ComponentFixture<TouristSpotCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TouristSpotCreateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TouristSpotCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
