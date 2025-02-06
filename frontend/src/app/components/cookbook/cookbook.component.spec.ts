import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CookbookComponent } from './cookbook.component';

describe('CookbookComponent', () => {
  let component: CookbookComponent;
  let fixture: ComponentFixture<CookbookComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CookbookComponent]
    });
    fixture = TestBed.createComponent(CookbookComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
