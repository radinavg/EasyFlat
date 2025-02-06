import {Component, OnInit} from '@angular/core';
import {StorageService} from "../../../services/storage.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ItemDto} from "../../../dtos/item";
import {ItemService} from "../../../services/item.service";
import {ToastrService} from "ngx-toastr";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ErrorHandlerService} from "../../../services/util/error-handler.service";

export enum QuantityChange {
  INCREASE = "increased",
  DECREASE = "decreased"
}

@Component({
  selector: 'app-item-detail-list',
  templateUrl: './item-detail-list.component.html',
  styleUrls: ['./item-detail-list.component.scss']
})
export class ItemDetailListComponent implements OnInit {
  itemGeneralName: string;
  filteredItems: ItemDto[];
  stockType: string = null;
  quantityInputs: { [itemId: number]: number } = {};

  constructor(private storageService: StorageService,
              private router: Router,
              private route: ActivatedRoute,
              private itemService: ItemService,
              private notification: ToastrService,
              private shoppingService: ShoppingListService,
              private errorHandler: ErrorHandlerService) {
  }

  ngOnInit(): void {
    this.reloadData();
  }

  reloadData(): void {
    this.route.queryParamMap.subscribe({
      next: queryParamMap => {
        this.stockType = queryParamMap.get('stockType');
        if(!this.stockType){
          this.router.navigate(['/digital-storage/']);
          this.notification.error(`Stock type was not recognized and could not be loaded.`, "Error");
        }
      },
      error: () => {
        this.router.navigate(['/digital-storage/']);
        this.notification.error(`Stock type was not recognized and could not be loaded.`, "Error");
      }
    })
    this.route.paramMap.subscribe({
      next: paramMap => {
        const generalName = paramMap.get('name');
        this.itemGeneralName = generalName
        this.itemService.findByGeneralName(generalName).subscribe({
          next: res => {
            if (res.length === 0) {
              this.router.navigate(['/digital-storage/']);
              this.notification.error(`Items of type ${generalName} could not be loaded.`, "Error");
            } else {
              if(this.stockType === 'in-stock'){
                this.filteredItems = res.filter(item => !item.alwaysInStock);
              } else {
                this.filteredItems = res.filter(item => item.alwaysInStock);
              }
            }
          },
          error: error => {
            this.router.navigate(['/digital-storage/']);
            this.errorHandler.handleErrors(error, "category " + generalName, "loaded");
          }
        })
      },
      error: () => {
        this.router.navigate(['/digital-storage/']);
        this.notification.error(`Items could not be loaded.`, "Error");
      }
    });
  }

  public changeItemQuantity(item: ItemDto, quantityInput: number, mode: QuantityChange): void {
    const previousCurrentQuantity: number = item.quantityCurrent;
    const previousTotalQuantity: number = item.quantityTotal;

    if (quantityInput == null) {
      this.notification.error("The quantity you provided is not valid.", "Error");
      this.quantityInputs[item.itemId] = 0;
      return;
    }

    if (quantityInput < 0.01) {
      this.notification.error("Only numbers greater than 0 can be entered.", "Error");
      this.quantityInputs[item.itemId] = null;
      return;
    }

    if(!(this.checkDecimalPlaces(quantityInput, item))){
      return;
    }

    quantityInput = parseFloat(quantityInput.toFixed(2));

    let isQuantityUpdated: boolean = false;

    const adjustment: number = mode === QuantityChange.INCREASE ? quantityInput : -quantityInput;

    const newQuantity: number = Math.max(0, item.quantityCurrent + adjustment);

    if (item.quantityCurrent !== newQuantity) {
      item.quantityCurrent = parseFloat(newQuantity.toFixed(2));
      isQuantityUpdated = true;
    }

    if (item.quantityCurrent > item.quantityTotal) {
      item.quantityTotal = item.quantityCurrent;
    }

    if (!isQuantityUpdated) {
      this.notification.info("The quantity is the same as before.", "Info");
      return;
    }

    this.itemService.updateItem(item).subscribe({
      next: () => {
        this.notification.success(`Item ${item.productName} was successfully ${mode} by ${quantityInput}.`, "Success");
        if( item.alwaysInStock && item.quantityCurrent < item.minimumQuantity){
          this.notification.success(`The item was automatically added to the shopping list.`, "Success");
        }
        if( !item.alwaysInStock && item.quantityCurrent <= 0 ){
          this.notification.success(`Item ${item.productName} has no stock and was successfully deleted.`, "Success");
          if(this.filteredItems.length <= 0){
            this.router.navigate(['/digital-storage/']);
          } else {
            this.reloadData();
          }
        }
      },
      error: error => {
        item.quantityCurrent = previousCurrentQuantity;
        item.quantityTotal = previousTotalQuantity;
        this.quantityInputs[item.itemId] = null;
        this.errorHandler.handleErrors(error, "item " + item.productName, mode);
      }
    });
  }

  public delete(item: ItemDto): void {
    this.itemService.deleteItem(item.itemId).subscribe({
      next: () => {
        if(this.filteredItems.length === 1){
          this.router.navigate(['/digital-storage/']);
        } else {
          this.reloadData();
        }
        this.notification.success(`Item ${item.productName} was successfully deleted.`, "Success");
      },
      error: error => {
        this.errorHandler.handleErrors(error, "item " + item.productName, "deleted");
      }
    });
  }

  getIdFormatForDeleteModal(item:ItemDto): string {
    return `${item.productName}${item.itemId.toString()}`.replace(/[^a-zA-Z0-9]+/g, '');
  }

  public showExpiryStatus(expireDate: Date): string {
    let daysForWarningSymbol: number = 3;
    let daysForDangerSymbol: number = 0;
    let today: Date = new Date();
    let expiry: Date = new Date(expireDate);
    let differenceInDays: number = Math.floor((Date.UTC(expiry.getFullYear(), expiry.getMonth(), expiry.getDate()) - Date.UTC(today.getFullYear(), today.getMonth(), today.getDate()) ) /(1000 * 3600 * 24));

    if (differenceInDays < daysForDangerSymbol) {
      return 'bi bi-x-circle-fill text-danger'; // Has already expired
    } else if (differenceInDays <= daysForWarningSymbol) {
      return 'bi bi-exclamation-triangle-fill text-warning'; // Expiring soon
    }
    return '';
  }

  public addToShoppingList(item: ItemDto): void {
    this.storageService.addItemToShoppingList(item).subscribe({
      next: () => {
        this.notification.success(`Item ${item.productName} successfully added to the shopping list.`, "Success");
        this.shoppingService.getShoppingListByName('Shopping List (Default)').subscribe({
          next: res => {
            this.router.navigate([`/shopping-lists/list/` + res.id]);
          }
        })
      },
      error: error => {
        this.errorHandler.handleErrors(error, "item " + item.productName, "added to shopping list");
      }
    });
  }

  private checkDecimalPlaces(quantityInput: number, item: ItemDto): boolean {
    const numberInputAsString: string = quantityInput.toString();
    const decimalIndex: number = numberInputAsString.indexOf('.');

    // check if number has at most 2 decimal places
    if (decimalIndex !== -1 && numberInputAsString.length - decimalIndex - 1 > 2) {
      this.notification.error("Only numbers with at most 2 decimal places can be entered.", "Error");
      this.quantityInputs[item.itemId] = null;
      return false;
    }

    return true;
  }

  protected readonly QuantityChange = QuantityChange;
}
