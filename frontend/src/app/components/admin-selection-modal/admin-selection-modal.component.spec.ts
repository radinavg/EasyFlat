import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminSelectionModalComponent } from './admin-selection-modal.component';

describe('AdminSelectionModalComponent', () => {
  let component: AdminSelectionModalComponent;
  let fixture: ComponentFixture<AdminSelectionModalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminSelectionModalComponent]
    });
    fixture = TestBed.createComponent(AdminSelectionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
