import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChorePreferenceComponent } from './chore-preference.component';

describe('ChorePreferenceComponent', () => {
  let component: ChorePreferenceComponent;
  let fixture: ComponentFixture<ChorePreferenceComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ChorePreferenceComponent]
    });
    fixture = TestBed.createComponent(ChorePreferenceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
