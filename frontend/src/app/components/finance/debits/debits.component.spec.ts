import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DebitsComponent} from './debits.component';

describe('DebitsComponent', () => {
    let component: DebitsComponent;
    let fixture: ComponentFixture<DebitsComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [DebitsComponent]
        });
        fixture = TestBed.createComponent(DebitsComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
