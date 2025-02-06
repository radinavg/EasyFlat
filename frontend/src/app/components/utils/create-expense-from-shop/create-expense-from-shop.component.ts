import {Component, Input} from '@angular/core';
import {ShoppingItemDto} from "../../../dtos/item";
import {DebitDto, ExpenseDto, SplitBy} from "../../../dtos/expenseDto";
import {AuthService} from "../../../services/auth.service";
import {UserService} from "../../../services/user.service";
import {FinanceService} from "../../../services/finance.service";
import {ErrorHandlerService} from "../../../services/util/error-handler.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-create-expense-from-shop',
  templateUrl: './create-expense-from-shop.component.html',
  styleUrls: ['./create-expense-from-shop.component.scss']
})
export class CreateExpenseFromShopComponent {
  @Input() checkedShoppingItems: ShoppingItemDto[] = []
  newExpenseTitle: string = null;
  newExpensePrice: number = null;

  constructor(private authService: AuthService,
              private userService: UserService,
              private financeService: FinanceService,
              private errorHandler: ErrorHandlerService,
              private notification: ToastrService) {
  }

  createExpense(): void {
    let dateInCET: Date = new Date();
    dateInCET.setHours(dateInCET.getHours() + 1);
    if (this.newExpenseTitle && (this.newExpensePrice != null || this.newExpensePrice != undefined) && this.checkedShoppingItems.length > 0) {
      let commaSeparatedList = this.checkedShoppingItems.map(item => item.productName).join(', ');

      this.authService.getUser(this.authService.getToken()).subscribe({
        next: activeUser => {
          this.userService.findFlatmates().subscribe({
            next: (users) => {
              let debitUsers: DebitDto[] = users.map(user => {
                return {
                  user: user,
                  splitBy: SplitBy.EQUAL,
                  value: this.newExpensePrice * 100 / users.length
                }
              });
              let expenseToCreate: ExpenseDto = {
                title: this.newExpenseTitle,
                description: 'Bought: ' + commaSeparatedList,
                amountInCents: this.newExpensePrice * 100,
                createdAt: dateInCET,
                paidBy: {
                  id: Number(activeUser.id),
                  firstName: activeUser.firstName,
                  lastName: activeUser.lastName,
                },
                debitUsers: debitUsers,
                items: null,
                isRepeating: false,
                periodInDays: null,
                repeatingExpenseType: null,
                addedViaStorage: true
              };
              this.financeService.createExpense(expenseToCreate).subscribe({
                next: () => {
                  this.notification.success(`Expense ${this.newExpenseTitle} successfully added to finance.`, "Success");
                },
                error: error => {
                  this.errorHandler.handleErrors(error, "expense " + this.newExpenseTitle, "added to finance");
                }
              });
            },
            error: error => {
              this.notification.error('Cannot find other flatmates, cannot add expense', "Error");
              this.errorHandler.handleErrors(error, "expense", "added");
            }
          });

        },
        error: error => {
          this.notification.error('Failed to load User, cannot add expense', "Error");
          this.errorHandler.handleErrors(error, "expense", "added");
        }
      });
    } else {
      if(!this.newExpenseTitle){
        this.notification.error(`A title needs to be set for the expense.`, "Error");
      }
      this.notification.error(`Expense could not be created.`, "Error");
    }
  }
}
