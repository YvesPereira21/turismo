import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpotManagerCreateComponent } from './spot-manager-create.component';

describe('SpotManagerCreateComponent', () => {
  let component: SpotManagerCreateComponent;
  let fixture: ComponentFixture<SpotManagerCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SpotManagerCreateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SpotManagerCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
