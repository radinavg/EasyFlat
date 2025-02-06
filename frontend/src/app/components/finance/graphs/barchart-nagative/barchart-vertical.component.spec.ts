import {ComponentFixture, TestBed} from '@angular/core/testing';

import {BarchartVerticalComponent} from './barchart-vertical.component';

describe('BarchartNagativeComponent', () => {
  let component: BarchartVerticalComponent;
  let fixture: ComponentFixture<BarchartVerticalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BarchartVerticalComponent]
    });
    fixture = TestBed.createComponent(BarchartVerticalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
