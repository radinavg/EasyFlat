import {ItemDto} from "./item";
import {UserListDto} from "./user";

export class ExpenseDto {
  id?: number;
  title?: string;
  description?: string;
  amountInCents?: number;
  createdAt?: Date;
  paidBy?: UserListDto;
  debitUsers?: DebitDto[];
  items?: ItemDto[];
  isRepeating?: boolean;
  periodInDays?: number;
  repeatingExpenseType?: RepeatingExpenseType;
  addedViaStorage?: boolean;
}

export class ExpenseSearchDto {
  title?: string;
  paidBy?: UserListDto;
  minAmountInEuro?: number;
  maxAmountInEuro?: number;
  fromDate?: Date;
  toDate?: Date;
}

export class DebitDto {
  user?: UserListDto;
  splitBy?: SplitBy;
  value?: number;
}

export class BalanceDebitDto {
  debtor?: UserListDto;
  creditor?: UserListDto;
  valueInCent?: number;
}

export class UserValuePairDto {
  user?: UserListDto;
  value?: number;
}

export enum SplitBy {
  EQUAL = "EQUAL",
  UNEQUAL = "UNEQUAL",
  PERCENTAGE = "PERCENTAGE",
  PROPORTIONAL = "PROPORTIONAL",
}

export enum RepeatingExpenseType {
  FIRST_OF_MONTH = "FIRST_OF_MONTH",
  FIRST_OF_QUARTER = "FIRST_OF_QUARTER",
  FIRST_OF_YEAR = "FIRST_OF_YEAR"
}

export enum RepeatingExpenseOptions {
  DAYS_UNTIL_REPEAT,
  NO_REPEAT,
  REPEAT_AT
}


