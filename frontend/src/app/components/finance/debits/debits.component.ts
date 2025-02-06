import {Component, EventEmitter, Input, Output} from '@angular/core';
import {BalanceDebitDto, ExpenseDto, SplitBy} from "../../../dtos/expenseDto";
import {UserListDto} from "../../../dtos/user";
import {FinanceService} from "../../../services/finance.service";
import {ToastrService} from "ngx-toastr";
import {ErrorHandlerService} from "../../../services/util/error-handler.service";

@Component({
    selector: 'app-debits',
    templateUrl: './debits.component.html',
    styleUrls: ['./debits.component.scss']
})
export class DebitsComponent {
    @Input() balanceDebits: BalanceDebitDto[] = [];
  @Input() activeUserId: number;

    @Output() reloadData = new EventEmitter<void>();

    constructor(
        private financeService: FinanceService,
        private notification: ToastrService,
        private errorHandlerService: ErrorHandlerService
    ) {
    }

    formatUserName(user: UserListDto): string {
        return user.firstName + ' ' + user.lastName;
    }

    convertAmountToEuro(amountInCent: number): string {
        return (amountInCent / 100.0).toFixed(2);
    }


  payback(debit: BalanceDebitDto) {
    let now = new Date();
    let expenseDto: ExpenseDto = {
      amountInCents: debit.valueInCent,
      title: "Payback",
      description: this.formatUserName(debit.debtor) + " pays back " + this.formatUserName(debit.creditor),
      paidBy: debit.debtor,
      createdAt: new Date(
        now.getFullYear(),
        now.getMonth(),
        now.getDate(),
        now.getHours() + 1,
        now.getMinutes(),
        now.getSeconds(),
        now.getMilliseconds()
      ),

            debitUsers: [
                {
                    user: debit.creditor,
                    value: debit.valueInCent,
                    splitBy: SplitBy.UNEQUAL
                }
            ],
        }

        this.financeService.createExpense(expenseDto).subscribe({
            next: (expense) => {
              this.notification.success("Payment successful", "Success");
                this.reloadData.emit();
            },
            error: (error) => {
              this.errorHandlerService.handleErrors(error, "payment", "created");
            }
        });

    }
}
