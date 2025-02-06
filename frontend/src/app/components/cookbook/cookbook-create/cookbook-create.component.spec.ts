import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CookbookCreateComponent } from './cookbook-create.component';

describe('CookbookCreateComponent', () => {
  let component: CookbookCreateComponent;
  let fixture: ComponentFixture<CookbookCreateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CookbookCreateComponent]
    });
    fixture = TestBed.createComponent(CookbookCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
