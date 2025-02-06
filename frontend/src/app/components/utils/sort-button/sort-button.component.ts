import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-sort-button',
  templateUrl: './sort-button.component.html',
  styleUrls: ['./sort-button.component.scss']
})
export class SortButtonComponent {
  @Input() isDescending: boolean = false;
  @Output() isDescendingChange = new EventEmitter<boolean>();

  toggleDirection() {
    this.isDescending = !this.isDescending;
    this.isDescendingChange.emit(this.isDescending);
  }
}
