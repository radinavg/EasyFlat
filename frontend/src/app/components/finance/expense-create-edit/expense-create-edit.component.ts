import {Component, OnInit} from '@angular/core';
import {ExpenseDto, RepeatingExpenseOptions, RepeatingExpenseType, SplitBy} from "../../../dtos/expenseDto";
import {NgForm} from "@angular/forms";
import {FinanceService} from "../../../services/finance.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {UserService} from "../../../services/user.service";
import {UserListDto} from "../../../dtos/user";
import {AuthService} from "../../../services/auth.service";
import {NgbDateStruct, NgbTimepickerConfig, NgbTimeStruct} from '@ng-bootstrap/ng-bootstrap';
import {Observable} from "rxjs";
import {ErrorHandlerService} from "../../../services/util/error-handler.service";

export enum ExpenseCreateEditMode {
  create,
  edit,
}

@Component({
  selector: 'app-expense-create-edit',
  templateUrl: './expense-create-edit.component.html',
  styleUrls: ['./expense-create-edit.component.scss']
})
export class ExpenseCreateEditComponent implements OnInit {

  mode: ExpenseCreateEditMode = ExpenseCreateEditMode.create;
  expense: ExpenseDto = {
    title: '',
    description: '',
    amountInCents: 0,
    debitUsers: [],
    paidBy: null,
    createdAt: null,
    isRepeating: false,
    repeatingExpenseType: null,
    periodInDays: 1,
  };
  amountInEuro: number;
  splitByOptions = Object.keys(SplitBy).map(key => ({value: key, label: SplitBy[key]}));
  selectedSplitBy: SplitBy = SplitBy.EQUAL;
  users: UserListDto[] = [];
  expenseDate: NgbDateStruct;
  expenseTime: NgbTimeStruct = {hour: 13, minute: 30, second: 0};
  selectedRepeatingOption: RepeatingExpenseOptions = RepeatingExpenseOptions.NO_REPEAT
  previousUrl: string;

  constructor(
    private userService: UserService,
    private financeService: FinanceService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    config: NgbTimepickerConfig,
    private errorHandlerService: ErrorHandlerService
  ) {
    config.spinners = false;
    this.previousUrl = (this.router.getCurrentNavigation().previousNavigation == null ? '/finance' : this.router.getCurrentNavigation().previousNavigation.finalUrl.toString());
  }

  public get heading(): string {
    switch (this.mode) {
      case ExpenseCreateEditMode.create:
        return 'Create expense';
      case ExpenseCreateEditMode.edit:
        return 'Editing expense';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case ExpenseCreateEditMode.create:
        return 'Create';
      case ExpenseCreateEditMode.edit:
        return 'Update';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === ExpenseCreateEditMode.create;
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case ExpenseCreateEditMode.create:
        return 'created';
      case ExpenseCreateEditMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });

    if (this.mode === ExpenseCreateEditMode.create) {
      this.userService.findFlatmates().subscribe({
        next: (users) => {
          this.expense.debitUsers = users.map(user => {
            return {
              user: user,
              splitBy: this.selectedSplitBy,
              value: 0
            }
          });
          this.users = users;
          this.onSplitByChange();

          this.authService.getUser(this.authService.getToken()).subscribe({
            next: (user) => {
              // TODO: this is a quickfix. The UserDetail should contain the ID of the user, but that's not the case
              this.expense.paidBy = this.users.find(u => u.firstName === user.firstName && u.lastName === user.lastName);
              this.expense.repeatingExpenseType = RepeatingExpenseType.FIRST_OF_MONTH;
            },
            error: (error) => {
              this.errorHandlerService.handleErrors(error, "user", "loaded");
            }
          });
        },
        error: (error) => {
          this.errorHandlerService.handleErrors(error, "flatmates", "loaded");
        }
      });
      let now = new Date();
      this.expenseDate = {year: now.getFullYear(), month: now.getMonth() + 1, day: now.getDate()}
      this.expenseTime = {hour: now.getHours(), minute: now.getMinutes(), second: now.getSeconds()};
    }

    if (this.mode === ExpenseCreateEditMode.edit) {
      this.route.params.subscribe({
        next: params => {
          const expenseId = params.id;
          this.financeService.findById(expenseId).subscribe({
            next: res => {
              this.expense = res;
              let date: Date = new Date(res.createdAt);
              this.expenseDate = {year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate()}
              this.expenseTime = {hour: date.getHours(), minute: date.getMinutes(), second: date.getSeconds()};
              this.amountInEuro = Math.ceil(res.amountInCents / 100 * 100) / 100;
              this.selectedSplitBy = res.debitUsers[0].splitBy;

              this.userService.findFlatmates().subscribe({
                next: (users) => {
                  this.users = users;
                },
                error: (error) => {
                  this.errorHandlerService.handleErrors(error, "flatmates", "loaded");
                }
              });
            },
            error: error => {
              this.router.navigate(['/expense']);
              this.errorHandlerService.handleErrors(error, "expense", "loaded");
            }
          })
        },
        error: error => {
          this.router.navigate(['/expense']);
          this.errorHandlerService.handleErrors(error, "expense", "edited");
        }
      });

    }
  }

  onSubmit(form: NgForm): void {
    if (this.checkIfAmountIsToHigh()) {
      return;
    }
    this.prepareExpense();

    if (form.valid) {
      let observable: Observable<ExpenseDto>;
      switch (this.mode) {
        case ExpenseCreateEditMode.create:
          observable = this.financeService.createExpense(this.expense);
          break;
        case ExpenseCreateEditMode.edit:
          observable = this.financeService.updateExpense(this.expense);
          break;
        default:
          console.error('Unknown ExpenseCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: () => {
          this.notification.success(`Expense ${this.expense.title} successfully ${this.modeActionFinished}.`, "Success");
          if (this.modeIsCreate) {
            this.router.navigate([this.previousUrl]);
          } else {
            this.router.navigate(['/expense']);
          }
        },
        error: (error) => {
          this.errorHandlerService.handleErrors(error, "expense", this.modeActionFinished);

          if (this.selectedSplitBy === SplitBy.EQUAL || this.selectedSplitBy === SplitBy.UNEQUAL) {
            this.expense.debitUsers.forEach(user => {
              user.value = user.value / 100;
            });
          }
        }
      });
    }
  }

  delete(): void {
    this.financeService.deleteExpense(this.expense.id).subscribe({
      next: (): void => {
        this.router.navigate(['/expense']);
        this.notification.success(`Expense ${this.expense.title} was successfully deleted`, "Success");
      },
      error: error => {
        this.errorHandlerService.handleErrors(error, "expense", "deleted");
      }
    });
  }

  getIdFormatForDeleteModal(expense: ExpenseDto): string {
    return `${expense.title}${expense.id.toString()}`.replace(/[^a-zA-Z0-9]+/g, '');
  }

  private prepareExpense() {
    this.expense.createdAt = new Date(
      this.expenseDate.year,
      this.expenseDate.month - 1,
      this.expenseDate.day,
      this.expenseTime.hour + 1,
      this.expenseTime.minute,
    );
    this.expense.amountInCents = this.roundToTwoDecimals(this.amountInEuro) * 100;
    if (this.selectedRepeatingOption != RepeatingExpenseOptions.REPEAT_AT) {
      this.expense.repeatingExpenseType = null;
    }
    if (this.selectedRepeatingOption == RepeatingExpenseOptions.NO_REPEAT) {
      this.expense.periodInDays = null;
    }
    if (this.selectedSplitBy === SplitBy.EQUAL || this.selectedSplitBy === SplitBy.UNEQUAL) {
      this.expense.debitUsers.forEach(user => {
        user.value = user.value * 100;
      });
    }
  }

  private checkIfAmountIsToHigh() {
    if (this.amountInEuro > 10_000) {
      this.notification.error("Amount too high. The maximum amount possible is 10.000", "Error");
      return true;
    }
    return false;
  }

  onSplitByChange() {
    this.expense.debitUsers.forEach(user => {
      user.splitBy = this.selectedSplitBy;
    });
  }

  onRepeatingChange() {
    this.expense.isRepeating = this.selectedRepeatingOption != RepeatingExpenseOptions.NO_REPEAT;
  }

  private roundToTwoDecimals(value: number): number {
    return Math.round(value * 100) / 100;
  }

  protected readonly RepeatingExpenseOptions = RepeatingExpenseOptions;
  protected readonly RepeatingExpenseType = RepeatingExpenseType;
}
