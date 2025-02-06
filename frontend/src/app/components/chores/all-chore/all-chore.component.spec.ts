import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllChoreComponent } from './all-chore.component';

describe('AllChoreComponent', () => {
  let component: AllChoreComponent;
  let fixture: ComponentFixture<AllChoreComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AllChoreComponent]
    });
    fixture = TestBed.createComponent(AllChoreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
