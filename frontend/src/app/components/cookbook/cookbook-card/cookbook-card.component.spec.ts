import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CookbookCardComponent } from './cookbook-card.component';

describe('CookbookCardComponent', () => {
  let component: CookbookCardComponent;
  let fixture: ComponentFixture<CookbookCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CookbookCardComponent]
    });
    fixture = TestBed.createComponent(CookbookCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
