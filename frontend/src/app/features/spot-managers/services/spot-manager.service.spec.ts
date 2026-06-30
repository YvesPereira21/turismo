import { TestBed } from '@angular/core/testing';

import { SpotManagerService } from './spot-manager.service';

describe('SpotManagerService', () => {
  let service: SpotManagerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SpotManagerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
