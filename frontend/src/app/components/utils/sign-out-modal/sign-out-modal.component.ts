import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-sign-out-modal',
  templateUrl: './sign-out-modal.component.html',
  styleUrls: ['./sign-out-modal.component.scss']
})
export class SignOutModalComponent {
  @Input() signOutInfo = '?';
  @Input() signOutName = '';
  @Input() signOutId = '';

  @Output() confirm = new EventEmitter<void>();

  constructor() {
  }

  openModal() {
    const modalId = `confirm-delete-modal${this.getIdFormatForDeleteModal()}`;
    const modal = document.getElementById(modalId);
    if (modal) {
      modal.classList.add('show');
      modal.style.display = 'block';
    }
  }

  close() {
    const modalId = `confirm-delete-modal${this.getIdFormatForDeleteModal()}`;
    const modal = document.getElementById(modalId);
    if (modal) {
      modal.classList.remove('show');
      modal.style.display = 'none';
    }
  }

  getIdFormatForDeleteModal(): string {
    return `${this.signOutName}${this.signOutId}`.replace(/\s/g, '');
  }
}
