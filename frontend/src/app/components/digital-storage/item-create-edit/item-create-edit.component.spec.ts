import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemCreateEditComponent } from './item-create-edit.component';

describe('ItemCreateComponent', () => {
  let component: ItemCreateEditComponent;
  let fixture: ComponentFixture<ItemCreateEditComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ItemCreateEditComponent]
    });
    fixture = TestBed.createComponent(ItemCreateEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
