import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-radio-buttons-component',
  templateUrl: './radio-buttons-component.component.html',
  styleUrls: ['./radio-buttons-component.component.scss']
})
export class RadioButtonsComponentComponent {
  @Input() bindingObject: any;
  @Input() options: { value: any; label: string }[];

  @Output() bindingObjectChange = new EventEmitter<any>();

  onBindingObjectChange(value: any) {
    this.bindingObjectChange.emit(value);
  }

}
