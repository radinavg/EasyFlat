import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyChoresComponent } from './my-chores.component';

describe('MyChoresComponent', () => {
  let component: MyChoresComponent;
  let fixture: ComponentFixture<MyChoresComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MyChoresComponent]
    });
    fixture = TestBed.createComponent(MyChoresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
