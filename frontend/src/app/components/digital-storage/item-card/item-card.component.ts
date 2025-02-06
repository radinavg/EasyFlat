import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-item-card',
  templateUrl: './item-card.component.html',
  styleUrls: ['./item-card.component.scss']
})

export class ItemCardComponent {
  @Input() id: string;
  @Input() title: string;
  @Input() quantity: number;
  @Input() quantityTotal: number;
  @Input() unit: string;

  constructor() {
  }

  getColorBasedOnQuantity(): string {
    const ratio = this.quantity / this.quantityTotal;
    if (ratio < 0.2) return 'bg-danger'; // Low quantity
    if (ratio < 0.4) return 'bg-warning'; // Medium quantity
    return 'bg-success'; // High quantity
  }

  getQuantityPercentage(): string {
    const percentage = (this.quantity / this.quantityTotal) * 100;
    return `${Math.max(0, Math.min(100, percentage))}%`; // Ensure percentage is between 0 and 100
  }

  truncateCategoryTitle(title: string): string {
    const maxCharLength: number = 15;
    return title.length > maxCharLength ? title.substring(0, maxCharLength) + '...' : title;
  }
}
