import {TestBed} from '@angular/core/testing';

import {OpenFoodFactService} from './open-food-fact.service';

describe('OpenFoodFactService', () => {
  let service: OpenFoodFactService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OpenFoodFactService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
