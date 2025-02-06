import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventsCreateComponent } from './events-create.component';

describe('EventsCreateComponent', () => {
  let component: EventsCreateComponent;
  let fixture: ComponentFixture<EventsCreateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EventsCreateComponent]
    });
    fixture = TestBed.createComponent(EventsCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
