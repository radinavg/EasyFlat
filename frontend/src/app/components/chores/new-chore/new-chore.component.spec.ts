import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewChoreComponent } from './new-chore.component';

describe('NewChoreComponent', () => {
  let component: NewChoreComponent;
  let fixture: ComponentFixture<NewChoreComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NewChoreComponent]
    });
    fixture = TestBed.createComponent(NewChoreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
