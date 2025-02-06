import { Component } from '@angular/core';
import {Observable} from "rxjs";
import {NgForm} from "@angular/forms";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {ShoppingListDto} from "../../../dtos/shoppingList";
import {ErrorHandlerService} from "../../../services/util/error-handler.service";

@Component({
  selector: 'app-shopping-list-create',
  templateUrl: './shopping-list-create.component.html',
  styleUrls: ['./shopping-list-create.component.scss']
})
export class ShoppingListCreateComponent {

  list: ShoppingListDto = {
    id: null,
    name: '',
    itemsCount: 0
  };
  constructor(
    private shoppingService: ShoppingListService,
    private router: Router,
    private notification: ToastrService,
    private errorHandler: ErrorHandlerService
  ) {
  }

  onSubmit(form: NgForm): void {
    if (form.valid) {
      let observable: Observable<ShoppingListDto>;
      observable = this.shoppingService.createList(this.list);
      observable.subscribe({
        next: data => {
          this.notification.success('Shopping list '+ this.list.name +' is successfully created.', "Success");
          this.router.navigate(['/shopping-lists']);
        },
        error: error => {
          this.errorHandler.handleErrors(error, "shopping list", 'create');
        }
      });
    }
  }
}
