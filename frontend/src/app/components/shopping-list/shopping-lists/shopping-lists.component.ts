import {Component, OnInit} from '@angular/core';
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ShoppingListDto} from "../../../dtos/shoppingList";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-shopping-lists',
  templateUrl: './shopping-lists.component.html',
  styleUrls: ['./shopping-lists.component.scss']
})
export class ShoppingListsComponent implements OnInit{
  lists: ShoppingListDto[];
  searchParams: string;
  showInput: boolean = false;

  constructor(private shoppingService: ShoppingListService,
              private notification: ToastrService) {
  }

  openInput() {
    this.showInput = true;
  }

  ngOnInit() {
    this.loadLists();
  }

  loadLists() {
    this.shoppingService.getShoppingLists(this.searchParams).subscribe({
      next: res => {
        this.lists = res;
      },
      error: err => {
        this.notification.error("Failed to loading shopping lists", 'Error')
      }
    })
  }
}
