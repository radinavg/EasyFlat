import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShoppingListCardComponent } from './shopping-list-card.component';

describe('ShoppingListCardComponent', () => {
  let component: ShoppingListCardComponent;
  let fixture: ComponentFixture<ShoppingListCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ShoppingListCardComponent]
    });
    fixture = TestBed.createComponent(ShoppingListCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
