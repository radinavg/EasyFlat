import {Component, OnInit} from '@angular/core';
import {BalanceDebitDto} from "../../dtos/expenseDto";
import {ToastrService} from "ngx-toastr";
import {FinanceService} from "../../services/finance.service";
import {ActivatedRoute} from "@angular/router";
import {Subject} from "rxjs";
import {UserDetail} from "../../dtos/auth-request";
import {AuthService} from "../../services/auth.service";
import {ErrorHandlerService} from "../../services/util/error-handler.service";

@Component({
  selector: 'app-finance',
  templateUrl: './finance.component.html',
  styleUrls: ['./finance.component.scss']
})
export class FinanceComponent implements OnInit {
  selectedGraphType: string = 'barchart';
  balanceDebits: BalanceDebitDto[] = [];
  reloadGraph: Subject<boolean> = new Subject<boolean>();
  activeUser: UserDetail;

  constructor(
    private financeService: FinanceService,
    private notification: ToastrService,
    private activatedRoute: ActivatedRoute,
    private authService: AuthService,
    private errorHandlerService: ErrorHandlerService
  ) {
  }

  ngOnInit(): void {
    this.findActiveUser();
    this.reloadData();
  }

  findActiveUser(): void {
    this.authService.getUser(this.authService.getToken()).subscribe({
      next: (user) => {
        this.activeUser = user;
      },
      error: (error) => {
        this.errorHandlerService.handleErrors(error, "user", "loaded");
      }
    });
  }

  reloadData() {
    this.reloadGraph.next(true);

    let o = this.financeService.findBalanceDebits().subscribe({
      next: (balanceDebits) => {
        this.balanceDebits = balanceDebits;
      },
      error: (error) => {
        this.errorHandlerService.handleErrors(error, "balance debits", "loaded");
      }
    })
  }


  protected readonly parseInt = parseInt;
}

