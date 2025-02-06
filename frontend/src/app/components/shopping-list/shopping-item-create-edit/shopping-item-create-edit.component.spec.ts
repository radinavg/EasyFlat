import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShoppingItemCreateEditComponent } from './shopping-item-create-edit.component';

describe('ItemCreateEditComponent', () => {
  let component: ShoppingItemCreateEditComponent;
  let fixture: ComponentFixture<ShoppingItemCreateEditComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ShoppingItemCreateEditComponent]
    });
    fixture = TestBed.createComponent(ShoppingItemCreateEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
