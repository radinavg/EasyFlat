import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ConfirmPayedPackComponent} from './confirm-payed-pack.component';

describe('ConfirmPayedPackComponent', () => {
  let component: ConfirmPayedPackComponent;
  let fixture: ComponentFixture<ConfirmPayedPackComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConfirmPayedPackComponent]
    });
    fixture = TestBed.createComponent(ConfirmPayedPackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
