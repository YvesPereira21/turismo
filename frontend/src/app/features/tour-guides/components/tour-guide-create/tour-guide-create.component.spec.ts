import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TourGuideCreateComponent } from './tour-guide-create.component';

describe('TourGuideCreateComponent', () => {
  let component: TourGuideCreateComponent;
  let fixture: ComponentFixture<TourGuideCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TourGuideCreateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TourGuideCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
