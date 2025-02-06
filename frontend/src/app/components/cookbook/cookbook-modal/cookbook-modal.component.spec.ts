import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CookbookModalComponent } from './cookbook-modal.component';

describe('CookbookModalComponent', () => {
  let component: CookbookModalComponent;
  let fixture: ComponentFixture<CookbookModalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CookbookModalComponent]
    });
    fixture = TestBed.createComponent(CookbookModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
