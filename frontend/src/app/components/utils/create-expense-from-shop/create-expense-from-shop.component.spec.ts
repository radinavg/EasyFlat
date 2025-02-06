import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateExpenseFromShopComponent } from './create-expense-from-shop.component';

describe('CreateExpenseFromShopComponent', () => {
  let component: CreateExpenseFromShopComponent;
  let fixture: ComponentFixture<CreateExpenseFromShopComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateExpenseFromShopComponent]
    });
    fixture = TestBed.createComponent(CreateExpenseFromShopComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
