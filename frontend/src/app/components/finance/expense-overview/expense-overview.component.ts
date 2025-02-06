import {Component, OnInit} from '@angular/core';
import {ExpenseDto, ExpenseSearchDto} from "../../../dtos/expenseDto";
import {FinanceService} from "../../../services/finance.service";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {debounceTime, Subject} from "rxjs";
import {UserListDto} from "../../../dtos/user";
import {UserService} from "../../../services/user.service";
import {ErrorHandlerService} from "../../../services/util/error-handler.service";

@Component({
  selector: 'app-expense-overview',
  templateUrl: './expense-overview.component.html',
  styleUrls: ['./expense-overview.component.scss']
})
export class ExpenseOverviewComponent implements OnInit {

  expenses: ExpenseDto[];
  searchParams: ExpenseSearchDto = {};
  searchFromDate: string | null = null;
  searchToDate: string | null = null;
  searchChangedObservable = new Subject<void>();
  users: UserListDto[] = [];

  constructor(private userService: UserService,
              private financeService: FinanceService,
              private router: Router,
              private notification: ToastrService,
              private errorHandler: ErrorHandlerService) {
  }

  searchChanged(): void {
    this.searchChangedObservable.next();
  }

  ngOnInit(): void {
    this.userService.findFlatmates().subscribe({
      next: (users) => {
        this.users = users;
      },
      error: (error) => {
        this.notification.error("Could not load flatmates", "Error");
        this.errorHandler.handleErrors(error, "flatmates", "loaded");
      }
    });
    this.reloadExpenses();
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.reloadExpenses()});
  }

  reloadExpenses() {
    if (this.searchFromDate == null || this.searchFromDate === "") {
      delete this.searchParams.fromDate;
    } else {
      this.searchParams.fromDate = new Date(this.searchFromDate);
    }
    if (this.searchToDate == null || this.searchToDate === "") {
      delete this.searchParams.toDate;
    } else {
      this.searchParams.toDate = new Date(this.searchToDate);
    }

    this.validateSearchParams();

    this.financeService.findAll(this.searchParams)
      .subscribe({
        next: res => {
          this.expenses = res;
        },
        error: error => {
          if (error.status === 422) {
            this.errorHandler.handleErrors(error, 'search', 'validated');
          } else {
            this.router.navigate(['/finance/']);
            this.errorHandler.handleErrors(error, "expenses", "loaded");
          }
        }
      });
  }

  public delete(expense: ExpenseDto): void {
    this.financeService.deleteExpense(expense.id).subscribe({
      next: (): void => {
        this.notification.success(`Expense ${expense.title} was successfully deleted`, "Success");
        this.reloadExpenses();
      },
      error: error => {
        this.errorHandler.handleErrors(error, "expense", "deleted");
      }
    });
  }

  validateSearchParams(): void {
    if (this.searchParams.title?.length > 160) {
      this.notification.error("The title search cannot contain more than 160 characters.", "Error");
      this.searchParams.title = null;

    } else if (this.searchParams.minAmountInEuro < 0) {
      this.notification.error("The minimum amount search cannot be less than 0 €.", "Error");
      this.searchParams.minAmountInEuro = 0;

    } else if (this.searchParams.minAmountInEuro > 10000) {
      this.notification.error("The minimum amount search cannot be larger than 10000 €.", "Error");
      this.searchParams.minAmountInEuro = 10000;

    } else if (this.searchParams.minAmountInEuro?.toString().length > 6) {
      this.notification.error("The minimum amount search cannot have more than 5 digits.", "Error");
      this.searchParams.minAmountInEuro = null;

    } else if (this.searchParams.maxAmountInEuro < 0) {
      this.notification.error("The maximum amount search cannot be less than 0 €.", "Error");
      this.searchParams.maxAmountInEuro = 0;

    } else if (this.searchParams.maxAmountInEuro > 10000) {
      this.notification.error("The maximum amount search cannot be larger than 10000 €.", "Error");
      this.searchParams.maxAmountInEuro = 10000;

    }  else if (this.searchParams.maxAmountInEuro?.toString().length > 6) {
      this.notification.error("The maximum amount search cannot have more than 5 digits.", "Error");
      this.searchParams.maxAmountInEuro = null;
    }
  }

  getIdFormatForDeleteModal(expense: ExpenseDto): string {
    return `${expense.title}${expense.id.toString()}`.replace(/[^a-zA-Z0-9]+/g, '');
  }
}
