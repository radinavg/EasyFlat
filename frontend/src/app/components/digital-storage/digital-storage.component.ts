import {Component, OnInit} from '@angular/core';
import {StorageService} from "../../services/storage.service";
import {ItemSearchDto, StorageItemListDto} from "../../dtos/storageItem";
import {OrderType} from "../../dtos/orderType";
import {ErrorHandlerService} from "../../services/util/error-handler.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-digital-storage',
  templateUrl: './digital-storage.component.html',
  styleUrls: ['./digital-storage.component.scss']
})

export class DigitalStorageComponent implements OnInit {
  items: StorageItemListDto[] = [];
  itemsAIS: StorageItemListDto[] = [];
  searchParameters: ItemSearchDto = {
    alwaysInStock: false,
    orderBy: OrderType.GENERAL_NAME,
    fillLevel: '',
    desc: false,
  };


  constructor(private storageService: StorageService,
              private notification: ToastrService,
              private errorHandler: ErrorHandlerService) {
  }

  ngOnInit() {
    this.loadStorage();
  }

  public loadStorage() {
    this.validateSearchParams();
    this.searchParameters.alwaysInStock = false;
    this.storageService.getItems(this.searchParameters).subscribe({
        next: res => {
          this.items = res;
        },
        error: error => {
          this.errorHandler.handleErrors(error, "in-stock items", "loaded");
        }
      }
    )
    this.searchParameters.alwaysInStock = true;
    this.storageService.getItems(this.searchParameters).subscribe({
        next: res => {
          this.itemsAIS = res;
        },
        error: error => {
          this.errorHandler.handleErrors(error, "always-in-stock items", "loaded");
        }
      }
    )
  }

  validateSearchParams(): void {
    if (this.searchParameters.productName?.length > 50) {
      this.notification.error("The title search cannot contain more than 50 characters.", "Error");
      this.searchParameters.productName = null;
    }
  }

  protected readonly OrderType = OrderType;
}
