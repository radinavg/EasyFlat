import {Component, OnInit} from '@angular/core';
import {NgForm} from "@angular/forms";
import {Observable, of} from "rxjs";
import {ShoppingItemDto} from "../../../dtos/item";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {Unit} from "../../../dtos/unit";
import {UnitService} from "../../../services/unit.service";
import {ShoppingListDto} from "../../../dtos/shoppingList";
import {ErrorHandlerService} from "../../../services/util/error-handler.service";

export enum ShoppingItemCreateEditMode {
  create,
  edit,
}

@Component({
  selector: 'app-item-create-edit',
  templateUrl: './shopping-item-create-edit.component.html',
  styleUrls: ['./shopping-item-create-edit.component.scss']
})
export class ShoppingItemCreateEditComponent implements OnInit {

  mode: ShoppingItemCreateEditMode = ShoppingItemCreateEditMode.create;
  item: ShoppingItemDto = {
    shoppingList: new ShoppingListDto(null, null, null),
    alwaysInStock: false,
    addToFiance: false
  }
  selectedLabelColor = '#ffffff';
  availableUnits: Unit[] = [];
  unitName: string;

  constructor(
    private shoppingService: ShoppingListService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private unitService: UnitService,
    private errorHandler: ErrorHandlerService
  ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case ShoppingItemCreateEditMode.create:
        return 'Create New Shopping Item';
      case ShoppingItemCreateEditMode.edit:
        return 'Editing Shopping Item';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case ShoppingItemCreateEditMode.create:
        return 'Create';
      case ShoppingItemCreateEditMode.edit:
        return 'Update';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === ShoppingItemCreateEditMode.create;
  }

  get modeIsEdit(): boolean {
    return this.mode === ShoppingItemCreateEditMode.edit;
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case ShoppingItemCreateEditMode.create:
        return 'created';
      case ShoppingItemCreateEditMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }


  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
      this.route.params.subscribe(params => {
        // Extract the 'id' parameter from the route
        const id = params['id'];
        this.shoppingService.getShoppingListById(id).subscribe({
          next: res => {
            this.item.shoppingList = res;
          }
        });
      });
    });

    this.unitService.findAll().subscribe({
      next: res => {
        this.availableUnits = res.filter((unit) => {
          return unit.name === "g" || unit.name === "kg" || unit.name === "ml" || unit.name === "l" || unit.name === "pcs";
        });
        this.item.unit = this.availableUnits[0];
      }
    });

    if (this.mode === ShoppingItemCreateEditMode.edit) {
      this.route.params.subscribe({
        next: params => {
          const itemId = params.id;
          this.shoppingService.getById(itemId).subscribe({
            next: res => {
              this.item = res;
              this.unitName = res.unit.name;
            },
            error: error => {
              this.router.navigate(['shopping-lists', 'list' + this.item.shoppingList.id]);
              this.notification.error('Item could not be retrieved', "Error");
            }
          })
        },
        error: error => {
          this.router.navigate(['shopping-lists', 'list' + this.item.shoppingList.id]);
          this.notification.error('No item provided for editing', "Error");
        }
      })
    }
  }


  public onSubmit(form: NgForm): void {

    if (form.valid) {
      let observable: Observable<ShoppingItemDto>;
      this.item.quantityCurrent = this.item.quantityTotal;
      switch (this.mode) {
        case ShoppingItemCreateEditMode.create:
          observable = this.shoppingService.createItem(this.item);
          break;
        case ShoppingItemCreateEditMode.edit:
          observable = this.shoppingService.updateItem(this.item);
          break;
        default:
          console.error('Unknown ItemCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Item ${this.item.productName} successfully ${this.modeActionFinished}.`, "Success");
          this.router.navigate(['shopping-lists', 'list', this.item.shoppingList.id]);
        },
        error: error => {
          this.errorHandler.handleErrors(error, "shopping item", this.modeActionFinished);
        }
      });
    }
  }

  addLabel(label: string, selectedLabelColor: string): void {
    if (!label || label.trim().length === 0) {
      this.notification.error("The label text cannot be blank", 'The data is not valid')
      return;
    }
    if (label.length > 10) {
      this.notification.error("The label text cannot have more than 10 characters", 'Tha data is not valid')
      return;
    }
    if (this.item.labels === undefined) {
      this.item.labels = [{
        labelValue: label,
        labelColour: (selectedLabelColor != '#ffffff' ? selectedLabelColor : '#000000'),
      }];
    } else {
      this.item.labels.push({
        labelValue: label,
        labelColour: (selectedLabelColor != '#ffffff' ? selectedLabelColor : '#000000'),
      });
    }
  }

  removeLabel(i: number) {
    this.item.labels.splice(i, 1);
  }


  formatUnitName(unit: Unit | null): string {
    return unit ? unit.name : '';
  }

  public compareUnitObjects(itemUnit: Unit, availableUnit: Unit): boolean {
    return itemUnit && availableUnit ? itemUnit.name === availableUnit.name : itemUnit === availableUnit;
  }
}
