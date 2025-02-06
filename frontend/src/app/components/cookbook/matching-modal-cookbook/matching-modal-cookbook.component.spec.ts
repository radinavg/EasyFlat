import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatchingModalCookbookComponent } from './matching-modal-cookbook.component';

describe('MatchingModalCookbookComponent', () => {
  let component: MatchingModalCookbookComponent;
  let fixture: ComponentFixture<MatchingModalCookbookComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MatchingModalCookbookComponent]
    });
    fixture = TestBed.createComponent(MatchingModalCookbookComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
