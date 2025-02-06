import {Component, OnInit} from '@angular/core';
import {ItemDto} from "../../../dtos/item";
import {ItemService} from "../../../services/item.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {ErrorHandlerService} from "../../../services/util/error-handler.service";

@Component({
  selector: 'app-item-detail',
  templateUrl: './item-detail.component.html',
  styleUrls: ['./item-detail.component.scss']
})
export class ItemDetailComponent implements OnInit {
  item: ItemDto = {
    itemId: 0,
    ean: "",
    generalName: "",
    productName: "",
    brand: "",
    quantityCurrent: 0,
    quantityTotal: 0,
    unit: {name: "g"},
    expireDate: new Date(),
    description: "",
    priceInCent: 0,
    alwaysInStock: false,
    addToFiance: false,
    ingredients: [{}],
  }

  constructor(
    private service: ItemService,
    private notification: ToastrService,
    private router: Router,
    private route: ActivatedRoute,
    private errorHandler: ErrorHandlerService,
  ) {
  }

  ngOnInit(): void {
    this.route.params.subscribe({
      next: params => {
        const itemId = params.id;
        this.service.getById(itemId).subscribe({
          next: res => {
            this.item = res;
          },
          error: error => {
            this.router.navigate(['/digital-storage/']);
            this.errorHandler.handleErrors(error, "item", "loaded");
          }
        })
      },
      error: () => {
        this.router.navigate(['/digital-storage/']);
        this.notification.error('Item could not be retrieved from the URL.', "Error");
      }
    });
  }

  get itemPrice(): string {
    return "€ " + (this.item.priceInCent / 100).toFixed(2).toString();
  }

  public delete() {
    this.service.deleteItem(this.item.itemId).subscribe({
      next: () => {
        this.router.navigate(['/digital-storage/']);
        this.notification.success(`Item ${this.item.productName} was successfully deleted.`, "Success");
      },
      error: error => {
        this.errorHandler.handleErrors(error, "item " + this.item.productName, "deleted");
      }
    });
  }

  getIdFormatForDeleteModal(item:ItemDto): string {
    return `${item.productName}${item.itemId.toString()}`.replace(/[^a-zA-Z0-9]+/g, '');
  }

}
