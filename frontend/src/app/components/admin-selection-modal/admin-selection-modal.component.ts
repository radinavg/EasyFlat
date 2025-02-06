import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-admin-selection-modal',
  templateUrl: './admin-selection-modal.component.html',
})
export class AdminSelectionModalComponent {
  @Input() users: any[]; // Assuming you have a User model/interface

  selectedUser: number;

  constructor(public activeModal: NgbActiveModal) {}

  confirmAdminSelection(): void {
    if (this.selectedUser) {
      this.activeModal.close(this.selectedUser);
    }
  }
}
