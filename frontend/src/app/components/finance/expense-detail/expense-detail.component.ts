import {Component, OnInit} from '@angular/core';
import {FinanceService} from "../../../services/finance.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {DebitDto, ExpenseDto, RepeatingExpenseType, SplitBy} from "../../../dtos/expenseDto";
import {ErrorHandlerService} from "../../../services/util/error-handler.service";

@Component({
  selector: 'app-expense-detail',
  templateUrl: './expense-detail.component.html',
  styleUrls: ['./expense-detail.component.scss']
})
export class ExpenseDetailComponent implements OnInit {


  expense: ExpenseDto;
  previousUrl: string;

  constructor(
    private financeService: FinanceService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private notification: ToastrService,
    private errorHandlingService: ErrorHandlerService
  ) {
    this.previousUrl = '/expense';
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe({
      next: params => {
        this.financeService.findById(params.id).subscribe({
          next: res => {
            this.expense = res;
          },
          error: error => {
            this.router.navigate([this.previousUrl]);
            this.errorHandlingService.handleErrors(error, "expense", "loaded");
          }
        });
      },
      error: error => {
        this.router.navigate([this.previousUrl]);
        this.errorHandlingService.handleErrors(error, "expense", "found");
      }
    });
  }

  delete(): void {
    this.financeService.deleteExpense(this.expense.id).subscribe({
          next: (): void => {
            this.router.navigate([this.previousUrl]);
            this.notification.success(`Expense ${this.expense.title} was successfully deleted`, "Success");
          },
          error: error => {
            this.errorHandlingService.handleErrors(error, "expense", "deleted");
          }
    });
  }

  getIdFormatForDeleteModal(expense: ExpenseDto): string {
    return `${expense.title}${expense.id.toString()}`.replace(/[^a-zA-Z0-9]+/g, '');
  }

  determineValueRepresentation(value: DebitDto): string {
    if (value.splitBy === SplitBy.EQUAL || value.splitBy === SplitBy.UNEQUAL) {
      return '€';
    }
    if (value.splitBy === SplitBy.PERCENTAGE) {
      return '%';
    }
    if (value.splitBy === SplitBy.PROPORTIONAL) {
      return 'Proportion';
    }
  }

  formatAmount(strategy: SplitBy, amount: number): string {
    if ([SplitBy.UNEQUAL, SplitBy.EQUAL].some(x => x === strategy)) {
      return (amount / 100).toFixed(2);
    }
    return amount.toFixed(2);
  }

  formatRepeatingExpenseType(repeatingExpenseType: RepeatingExpenseType): string {
    switch (repeatingExpenseType) {
      case RepeatingExpenseType.FIRST_OF_MONTH:
        return "First of Month";
      case RepeatingExpenseType.FIRST_OF_QUARTER:
        return "First of Quarter";
      case RepeatingExpenseType.FIRST_OF_YEAR:
        return "First of Year";
    }
  }

  protected readonly SplitBy = SplitBy;

}
