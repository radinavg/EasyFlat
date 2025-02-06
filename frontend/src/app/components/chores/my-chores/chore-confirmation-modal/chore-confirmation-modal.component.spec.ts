import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChoreConfirmationModalComponent } from './chore-confirmation-modal.component';

describe('ChoreConfirmationModalComponent', () => {
  let component: ChoreConfirmationModalComponent;
  let fixture: ComponentFixture<ChoreConfirmationModalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ChoreConfirmationModalComponent]
    });
    fixture = TestBed.createComponent(ChoreConfirmationModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
