import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginFlatComponent } from './login-flat.component';

describe('LoginFlatComponent', () => {
  let component: LoginFlatComponent;
  let fixture: ComponentFixture<LoginFlatComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LoginFlatComponent]
    });
    fixture = TestBed.createComponent(LoginFlatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
