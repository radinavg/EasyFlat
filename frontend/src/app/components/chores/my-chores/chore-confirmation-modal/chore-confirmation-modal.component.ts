// chore-confirmation-modal.component.ts
import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ChoreService } from "../../../../services/chore.service";

@Component({
  selector: 'app-chore-confirmation-modal',
  templateUrl: './chore-confirmation-modal.component.html',
  styleUrls: ['./chore-confirmation-modal.component.scss']
})
export class ChoreConfirmationModalComponent {
  @Input() choreName: string;
  repeatDate: string;
  repeatChore: boolean = false;
  result: any = false; // Initialize result variable

  constructor(public activeModal: NgbActiveModal, public choresService: ChoreService) {
    this.repeatDate = this.getNextWeekDate();
    console.log(this.repeatDate);
  }

  closeModal() {
    this.activeModal.close({ deleteChore: false, repeatChore: false });
  }

  confirmDelete() {
    // Set result to false for Delete
    this.activeModal.close(false);
  }

  confirmRepeat() {
    // Set result to true for Repeat
    this.result = true;
    this.activeModal.close({ repeat: true, date: this.repeatDate});
  }

  private getNextWeekDate(): string {
    const today = new Date();
    const nextWeek = new Date(today.setDate(today.getDate() + 7));
    const year = nextWeek.getFullYear();
    const month = String(nextWeek.getMonth() + 1).padStart(2, '0');
    const day = String(nextWeek.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
