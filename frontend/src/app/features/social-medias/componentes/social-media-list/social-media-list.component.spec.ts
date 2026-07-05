import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SocialMediaListComponent } from './social-media-list.component';

describe('SocialMediaListComponent', () => {
  let component: SocialMediaListComponent;
  let fixture: ComponentFixture<SocialMediaListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SocialMediaListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SocialMediaListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
