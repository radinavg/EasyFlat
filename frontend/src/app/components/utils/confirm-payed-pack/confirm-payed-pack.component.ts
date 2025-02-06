import {Component, EventEmitter, Input, Output} from '@angular/core';
import {BalanceDebitDto} from "../../../dtos/expenseDto";
import {UserListDto} from "../../../dtos/user";

@Component({
  selector: 'app-confirm-payed-pack',
  templateUrl: './confirm-payed-pack.component.html',
  styleUrls: ['./confirm-payed-pack.component.scss']
})
export class ConfirmPayedPackComponent {
  @Input() mId: number;
  @Input() debit: BalanceDebitDto;

  @Output() confirm = new EventEmitter<void>();

  constructor() {
  }

  formatUserName(user: UserListDto): string {
    return user.firstName + ' ' + user.lastName;
  }

  convertAmountToEuro(amountInCent: number): string {
    return (amountInCent / 100.0).toFixed(2);
  }
}
