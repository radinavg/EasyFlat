import {Component, OnInit} from '@angular/core';
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {ItemDto, ShoppingItemDto, ShoppingItemSearchDto} from "../../dtos/item";
import {ShoppingListService} from "../../services/shopping-list.service";
import {ShoppingListDto} from "../../dtos/shoppingList";
import {ErrorHandlerService} from "../../services/util/error-handler.service";

@Component({
  selector: 'app-shopping-list',
  templateUrl: './shopping-list.component.html',
  styleUrls: ['./shopping-list.component.scss']
})
export class ShoppingListComponent implements OnInit {

  shoppingList: ShoppingListDto = {
    id: 0,
    name: '',
    itemsCount: 0
  };
  items: ShoppingItemDto[] = [];
  shopId: string;
  checkedItems: ShoppingItemDto[] = [];
  selectedShoppingListId: number;
  shoppingLists: ShoppingListDto[] = [];
  searchParams: ShoppingItemSearchDto = {
    productName: '',
    label: '',
  }
  baseUri: string = 'shopping-lists/list';

  constructor(
    private shoppingListService: ShoppingListService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private errorHandler: ErrorHandlerService
  ) {
  }

  ngOnInit(): void {
    this.shoppingListService.getShoppingLists('').subscribe({
      next: res => {
        this.shoppingLists = res;
        this.route.params.subscribe({
          next: params => {
            this.shopId = params.id;
            this.shoppingListService.getShoppingListById(this.shopId).subscribe({
              next: (res: ShoppingListDto) => {
                this.shoppingList = res;
                this.selectedShoppingListId = res.id;
                this.getItems();
              },
              error: (error: any) => {
                this.errorHandler.handleErrors(error, "shopping list", 'get');
              }
            });
          }
        });
      },
      error: error => {
        this.errorHandler.handleErrors(error, "shopping lists", 'get');
      }
    });
    this.checkedItems = [];
  }

  getItems() {
    this.shoppingListService.getItemsWithShopId(this.shopId, this.searchParams).subscribe({
      next: res => {
        this.items = res;
      },
      error: error => {
        this.errorHandler.handleErrors(error, "shopping items", 'get');
      }
    });
  }

  navigateToCreateItem() {
    this.router.navigate([this.baseUri, this.shoppingList.id, 'item', 'create']);
  }

  deleteList() {
    console.log(this.shopId)
    this.shoppingListService.deleteList(this.shopId).subscribe({
      next: (deletedList: ShoppingListDto) => {
        this.router.navigate(['shopping-lists']);
        this.notification.success('Shopping list ' + deletedList.name + " was successfully deleted.", "Success");
      },
      error: error => {
        this.errorHandler.handleErrors(error, "shopping list", 'delete');
      }
    });
  }

  updateCheckedItems(item: ShoppingItemDto) {
    item.check = !item.check;
    this.checkedItems = this.getCheckedItems();
  }

  getCheckedItems(): ShoppingItemDto[] {
    return this.items.filter(item => item.check);
  }

  deleteCheckedItems() {
    const checkedItems = this.checkedItems.slice();

    if (checkedItems.length === 0) {
      this.notification.info('Please, mark the items you want to delete');
      return;
    }
    this.shoppingListService.deleteItems(checkedItems).subscribe({
      next: () => {
        this.notification.success("Items were successfully deleted from the list", "Success");
        this.ngOnInit();
      },
      error: error => {
        this.errorHandler.handleErrors(error, "shopping item", 'delete');
      }
    });

  }

  onShoppingListChange() {
    if (this.selectedShoppingListId) {
      this.shoppingListService.getShoppingListById(this.selectedShoppingListId + '').subscribe({
        next: res => {
          this.shoppingList = res;
          this.shopId = res.id + '';
          this.getItems();
          this.router.navigate([this.baseUri, this.shopId]);
        }
      });
    }
  }

  transferToStorage() {
    this.shoppingListService.transferToStorage(this.checkedItems).subscribe({
        next: data => {
          this.notification.success(`Items successfully added to the storage.`);
          this.router.navigate([`/digital-storage`])
        },
        error: err => {
          this.errorHandler.handleErrors(err, "shopping-item", "created");
          if (err.status === 409) {
            this.notification.error("Either change the unit or the category", 'Error');
          }
        }
      }
    );
  }

  checkIsEmpty() {
    return this.checkedItems.length == 0;
  }

  checkId() {
    return this.shoppingList.name == "Shopping List (Default)";
  }

  navigateToEditItem(itemId: string) {
    this.router.navigate([this.baseUri, this.shopId, 'item', itemId, 'edit']);
  }

  getIdFormatForDeleteModal(item: ItemDto): string {
    return `${item.productName}${item.itemId.toString()}`.replace(/[^a-zA-Z0-9]+/g, '');
  }

  getIdFormatForDeleteModalForList(shoppingList: ShoppingListDto) {
    return `${shoppingList.name}${shoppingList.id.toString()}`.replace(/[^a-zA-Z0-9]+/g, '');
  }

  getIdFormatForDeleteModalForChecked(checkedItems: ShoppingItemDto[]) {
    return `items-1243`.replace(/[^a-zA-Z0-9]+/g, '');
  }

  truncateString(input: string, maxLength: number): string {
    if (input.length <= maxLength) {
      return input;
    }
    const truncated = input.substring(0, maxLength - 3);
    return truncated + '...';
  }

}
