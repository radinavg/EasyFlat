import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatchingModalComponent } from './matching-modal.component';

describe('MatchingModalComponent', () => {
  let component: MatchingModalComponent;
  let fixture: ComponentFixture<MatchingModalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MatchingModalComponent]
    });
    fixture = TestBed.createComponent(MatchingModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
