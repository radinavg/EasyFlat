import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpParams} from "@angular/common/http";
import {BalanceDebitDto, ExpenseDto, ExpenseSearchDto, UserValuePairDto} from "../dtos/expenseDto";
import {Observable} from "rxjs";
import {formatDate} from "@angular/common";
import {tap} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class FinanceService {
  baseUri = environment.backendUrl + '/expense';

  constructor(
    private http: HttpClient,
  ) {
  }

  createExpense(expense: ExpenseDto): Observable<ExpenseDto> {
    return this.http.post<ExpenseDto>(this.baseUri, expense);
  }

  updateExpense(expense: ExpenseDto): Observable<ExpenseDto> {
    return this.http.put<ExpenseDto>(`${this.baseUri}/${expense.id}`, expense);
  }

  deleteExpense(expenseId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUri}/${expenseId}`);
  }

  findTotalExpensesPerUser(): Observable<UserValuePairDto[]> {
    return this.http.get<UserValuePairDto[]>(this.baseUri + '/statistics/expenses');
  }

  findTotalDebitsPerUser(): Observable<UserValuePairDto[]> {
    return this.http.get<UserValuePairDto[]>(this.baseUri + '/statistics/debits');
  }

  findBalanceExpenses(): Observable<UserValuePairDto[]> {
    return this.http.get<UserValuePairDto[]>(this.baseUri + '/statistics/balance');
  }

  findBalanceDebits(): Observable<BalanceDebitDto[]> {
    return this.http.get<BalanceDebitDto[]>(this.baseUri + '/debits');
  }

  /**
   * Find expense with given id.
   *
   * @param id of the expense
   */
  findById(id: number): Observable<ExpenseDto> {
    return this.http.get<ExpenseDto>(this.baseUri + '/' + id);
  }

  findAll(searchParams: ExpenseSearchDto): Observable<ExpenseDto[]> {
    if (searchParams.title === '') {
      delete searchParams.title;
    }
    let params = new HttpParams();
    if (searchParams.title) {
      params = params.append('title', searchParams.title);
    }
    if (searchParams.paidBy) {
      params = params.append('paidById', searchParams.paidBy.id);
    }
    if (searchParams.minAmountInEuro) {
      params = params.append('minAmountInCents', searchParams.minAmountInEuro * 100);
    }
    if (searchParams.maxAmountInEuro) {
      params = params.append('maxAmountInCents', searchParams.maxAmountInEuro * 100);
    }
    if (searchParams.fromDate) {
      params = params.append('fromCreatedAt', formatDate(searchParams.fromDate, 'dd-MM-yyyy', 'en-US'));
    }
    if (searchParams.toDate) {
      params = params.append('toCreatedAt', formatDate(searchParams.toDate, 'dd-MM-yyyy', 'en-US'));
    }

    return this.http.get<ExpenseDto[]>(this.baseUri, {params})
      .pipe(tap(expenses => expenses.map(e => {
        e.createdAt = new Date(e.createdAt); // Parse date string
      })));
  }
}
