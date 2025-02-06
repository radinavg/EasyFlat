import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CookbookDetailComponent } from './cookbook-detail.component';

describe('CookbookDetailComponent', () => {
  let component: CookbookDetailComponent;
  let fixture: ComponentFixture<CookbookDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CookbookDetailComponent]
    });
    fixture = TestBed.createComponent(CookbookDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
