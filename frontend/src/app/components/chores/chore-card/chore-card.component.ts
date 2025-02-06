import { Component, Input } from '@angular/core';
import {UserDetail} from "../../../dtos/auth-request";

@Component({
  selector: 'app-chore-card',
  templateUrl: './chore-card.component.html',
  styleUrls: ['./chore-card.component.scss']
})
export class ChoreCardComponent {
  @Input() name: string;
  @Input() description: string;
  @Input() endDate: Date;
  @Input() points: string;
  @Input() user: string;
  @Input() completed: boolean;
  @Input() mode: number;
  truncated: boolean;

  ngOnInit() {
    this.truncated = this.mode === 0
  }

  getColorBasedOnDeadline(): string {
    let daysForWarning: number = 3;
    let daysForDanger: number = 1;
    let today: Date = new Date();
    let deadline: Date = new Date(this.endDate);
    let differenceInDays: number = Math.floor((Date.UTC(deadline.getFullYear(), deadline.getMonth(), deadline.getDate()) - Date.UTC(today.getFullYear(), today.getMonth(), today.getDate()) ) /(1000 * 3600 * 24));

    if (differenceInDays <= daysForDanger) {
      return 'text-danger';
    } else if (differenceInDays <= daysForWarning) {
      return 'text-warning';
    }
    return 'text-success';
  }

  getTruncated(text: string, maxLength: number) {
    if (this.truncated) {
    return text.length > maxLength ?
      text.slice(0, maxLength) + '...' :
      text;
    } else {
      return text;
    }
  }

  toggleTruncate() {
    this.truncated = !this.truncated;
  }
}
