import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {DebitDto, SplitBy} from "../../../dtos/expenseDto";
import {ExpenseCreateEditMode} from "../../finance/expense-create-edit/expense-create-edit.component";

@Component({
  selector: 'app-show-user-for-expense',
  templateUrl: './show-user-for-expense.component.html',
  styleUrls: ['./show-user-for-expense.component.scss']
})
export class ShowUserForExpenseComponent implements OnChanges {
  @Input() amountInEuro: number;
  @Input() splitBy: SplitBy;
  @Input() users: DebitDto[];
  @Input() mode: ExpenseCreateEditMode;

  @Output() usersChange = new EventEmitter<DebitDto[]>();

  selectedUsers: boolean[] = [];

  private isInitialDataLoaded: number = 0;

  onUsersChange() {
    this.usersChange.emit(this.users);
  }

  ngOnChanges(): void {
    if (!this.isInitialDataLoaded && this.mode === ExpenseCreateEditMode.edit) {
      this.initializeSelectedUsersArray();
      this.users.forEach(user => {
        if (this.splitBy == SplitBy.EQUAL || this.splitBy == SplitBy.UNEQUAL) {
          user.value = this.roundToTwoDecimals(user.value / 100);
        }
      })
      this.isInitialDataLoaded++;
      return;
    }
    if (this.isInitialDataLoaded >= 1 || this.mode === ExpenseCreateEditMode.create) {
      this.updateSelectedUsersArray();
      this.adaptedToChange();
    }
  }

  determineValueRepresentation(value: DebitDto): string {
    if (this.splitBy === SplitBy.EQUAL || this.splitBy === SplitBy.UNEQUAL) {
      return '€';
    }
    if (this.splitBy === SplitBy.PERCENTAGE) {
      return '%';
    }
    if (this.splitBy === SplitBy.PROPORTIONAL) {
      return 'Proportion';
    }
  }

  adaptedToChange() {
    if (this.splitBy === SplitBy.EQUAL) {
      this.users.forEach(user => {
        if (this.selectedUsers[this.users.indexOf(user)]) {
          user.value = this.roundToTwoDecimals((this.amountInEuro ? this.amountInEuro : 0) / this.sizeOfSelectedUsers());
        } else {
          user.value = 0;
        }
      })
    } else if (this.splitBy === SplitBy.PERCENTAGE) {
      this.users.forEach(user => {
        if (this.selectedUsers[this.users.indexOf(user)]) {
          user.value = this.roundToTwoDecimals((100 / this.sizeOfSelectedUsers()));
        } else {
          user.value = 0;
        }
      })
    } else {
      this.users.forEach(user => {
        user.value = 0
      })
    }
  }

  private sizeOfSelectedUsers(): number {
    return this.selectedUsers.filter(value => value).length;
  }
  private initializeSelectedUsersArray() {
    this.users.forEach((value, index) => {
      this.selectedUsers[index] = value.value !== 0;
    });
  }

  private updateSelectedUsersArray() {
    this.users.forEach((value, index) => {
      this.selectedUsers[index] = this.selectedUsers[index] !== false;
    });
  }

  private roundToTwoDecimals(value: number): number {
    return Math.round(value * 100) / 100;
  }

  protected readonly SplitBy = SplitBy;
}
