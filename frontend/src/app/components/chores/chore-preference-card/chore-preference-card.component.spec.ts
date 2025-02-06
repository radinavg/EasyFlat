import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChorePreferenceCardComponent } from './chore-preference-card.component';

describe('ChorePreferenceCardComponent', () => {
  let component: ChorePreferenceCardComponent;
  let fixture: ComponentFixture<ChorePreferenceCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ChorePreferenceCardComponent]
    });
    fixture = TestBed.createComponent(ChorePreferenceCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
