import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ShowUserForExpenseComponent} from './show-user-for-expense.component';

describe('ShowUserForExpenseComponent', () => {
  let component: ShowUserForExpenseComponent;
  let fixture: ComponentFixture<ShowUserForExpenseComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ShowUserForExpenseComponent]
    });
    fixture = TestBed.createComponent(ShowUserForExpenseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
