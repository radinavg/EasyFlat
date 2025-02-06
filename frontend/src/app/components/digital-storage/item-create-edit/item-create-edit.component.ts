import {Component, OnInit, ViewChild} from '@angular/core';
import {ItemDto} from "../../../dtos/item";
import {NgForm} from "@angular/forms";
import {ItemService} from "../../../services/item.service";
import {ToastrService} from "ngx-toastr";
import {DigitalStorageDto} from "../../../dtos/digitalStorageDto";
import {Observable, of} from "rxjs";
import {StorageService} from "../../../services/storage.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Unit} from "../../../dtos/unit";
import {UnitService} from "../../../services/unit.service";
import {NgxScannerQrcodeComponent, ScannerQRCodeResult} from "ngx-scanner-qrcode";
import {OpenFoodFactService} from "../../../services/open-food-fact.service";
import {FinanceService} from "../../../services/finance.service";
import {DebitDto, ExpenseDto, SplitBy} from "../../../dtos/expenseDto";
import {AuthService} from "../../../services/auth.service";
import {UserService} from "../../../services/user.service";
import {ErrorHandlerService} from "../../../services/util/error-handler.service";

export enum ItemCreateEditMode {
  create,
  edit,
}

@Component({
  selector: 'app-item-create-edit',
  templateUrl: './item-create-edit.component.html',
  styleUrls: ['./item-create-edit.component.scss']
})
export class ItemCreateEditComponent implements OnInit {

  @ViewChild('action')
  scanner: NgxScannerQrcodeComponent;

  mode: ItemCreateEditMode = ItemCreateEditMode.create;
  item: ItemDto = {
    alwaysInStock: false,
    addToFiance: false,
    boughtAt: '',
    unit: {
      name: ''
    },
    priceInCent: null,
  }
  priceInEuro: number = 1.00;
  availableUnits: Unit[] = [];


  constructor(
    private itemService: ItemService,
    private storageService: StorageService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private unitServ: UnitService,
    private openFoodFactService: OpenFoodFactService,
    private financeService: FinanceService,
    private authService: AuthService,
    private userService: UserService,
    private errorHandler: ErrorHandlerService
  ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case ItemCreateEditMode.create:
        return 'Create a new item';
      case ItemCreateEditMode.edit:
        return 'Editing item';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case ItemCreateEditMode.create:
        return 'Create';
      case ItemCreateEditMode.edit:
        return 'Update';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === ItemCreateEditMode.create;
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case ItemCreateEditMode.create:
        return 'created';
      case ItemCreateEditMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }

  ngOnInit(): void {
    this.unitServ.findAll().subscribe({
      next: res => {
        this.availableUnits = res.filter((unit) => {
          return unit.name === "g" || unit.name === "kg" || unit.name === "ml" || unit.name === "l" || unit.name === "pcs";
        });
        this.item.unit = this.availableUnits[0];
      },
      error: error => {
        this.errorHandler.handleErrors(error, "units", "loaded");
      }
    });

    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });

    if (this.mode === ItemCreateEditMode.edit) {
      this.route.params.subscribe({
        next: params => {
          const itemId = params.id;
          this.itemService.getById(itemId).subscribe({
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
          this.notification.error('Item could not be loaded for editing.', "Error");
        }
      })
    }

    if (this.mode === ItemCreateEditMode.create) {
      this.storageService.findAll('', 1).subscribe({
        next: res => {
          this.item.digitalStorage = res[0];
        },
        error: error => {
          this.errorHandler.handleErrors(error, "storage", "loaded");
        }
      });
    }
  }

  public onSubmit(form: NgForm): void {
    this.item.priceInCent = this.item.addToFiance ? this.priceInEuro * 100 : null;
    if (this.item.ean == '') {
      this.item.ean = null;
    }

    if (form.valid) {
      let observable: Observable<ItemDto>;
      switch (this.mode) {
        case ItemCreateEditMode.create:
          this.item.quantityCurrent = this.item.quantityTotal;
          observable = this.itemService.createItem(this.item);
          break;
        case ItemCreateEditMode.edit:
          observable = this.itemService.updateItem(this.item);
          break;
        default:
          console.error('Unknown ItemCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: () => {
          if (this.item.addToFiance) {
            this.createExpenseFromItemDto();
          }

          if (this.modeIsCreate) {
            this.notification.success(`Item ${this.item.productName} successfully ${this.modeActionFinished} and added to the storage.`, "Success");
          } else {
            this.notification.success(`Item ${this.item.productName} successfully ${this.modeActionFinished}.`, "Success");
          }
          if (!this.modeIsCreate && this.item.alwaysInStock && this.item.quantityCurrent < this.item.minimumQuantity) {
            this.notification.success(`The item was automatically added to the shopping list.`, "Success");
          }
          if (!this.modeIsCreate && !this.item.alwaysInStock && this.item.quantityCurrent <= 0) {
            this.notification.success(`Item ${this.item.productName} has no stock and was successfully deleted.`, "Success");
          }

          this.router.navigate(['/digital-storage']);
        },
        error: error => {
          this.errorHandler.handleErrors(error, "item", this.modeActionFinished);
        }
      });
    }
  }

  private createExpenseFromItemDto() {
    this.authService.getUser(this.authService.getToken()).subscribe({
      next: activeUser => {
        this.userService.findFlatmates().subscribe({
          next: (users) => {
            let debitUsers: DebitDto[] = users.map(user => {
              return {
                user: user,
                splitBy: SplitBy.EQUAL,
                value: this.priceInEuro * 100 / users.length
              }
            });
            let expenseToCreate: ExpenseDto = {
              title: 'Bought ' + this.item.productName,
              description: 'Bought ' + this.item.quantityCurrent
                + ' ' + this.item.unit.name
                + ' of ' + this.item.productName
                + ' for ' + this.priceInEuro
                + ' €'
                + (this.item.boughtAt != null && this.item.boughtAt != ''
                  ? ' at ' + this.item.boughtAt
                  : ''),
              amountInCents: this.item.priceInCent,
              createdAt: new Date(),
              paidBy: {
                id: Number(activeUser.id),
                firstName: activeUser.firstName,
                lastName: activeUser.lastName,
              },
              debitUsers: debitUsers,
              items: [this.item],
              isRepeating: false,
              periodInDays: null,
              repeatingExpenseType: null,
              addedViaStorage: true
            };
            this.financeService.createExpense(expenseToCreate).subscribe({
              next: () => {
                this.notification.success(`Item ${this.item.productName} successfully added to finance.`, "Success");
              },
              error: error => {
                this.errorHandler.handleErrors(error, "item " + this.item.productName, "added to finance");
              }
            });
          },
          error: error => {
            this.notification.error('Cannot find other flatmates, cannot add expense', "Error");
            this.errorHandler.handleErrors(error, "expense", "added");
          }
        });

      },
      error: error => {
        this.notification.error('Failed to load User, cannot add expense', "Error");
        this.errorHandler.handleErrors(error, "expense", "added");
      }
    });
  }


  addIngredient(ingredient: string): void {
    if (ingredient == undefined || ingredient.length == 0) {
      return
    }
    if (this.item.ingredients === undefined) {
      this.item.ingredients = [{name: ingredient}];
    } else {
      this.item.ingredients.push({name: ingredient});
    }
  }

  removeIngredient(i: number) {
    this.item.ingredients.splice(i, 1);
  }

  formatStorageName(storage: DigitalStorageDto | null): string {
    return storage ? storage.title : '';
  }

  storageSuggestions = (input: string) => (input === '')
    ? of([])
    : this.storageService.findAll(input, 5);

  formatGeneralName(generalName: ItemDto | string | null): string {
    if (typeof generalName === 'string') {
      return generalName;
    }
    return generalName != null ? generalName.generalName : '';
  }

  generalNameSuggestions = (input: string) => (input === '')
    ? this.itemService.findAll(5)
    : this.itemService.findByGeneralName(input);

  formatBrand(brand: ItemDto | string | null): string {
    if (typeof brand === 'string') {
      return brand;
    }
    return brand ? brand.brand : '';
  }

  brandSuggestions = (input: string) => (input === '')
    ? this.itemService.findAll(5)
    : this.itemService.findByBrand(input);

  formatBoughtAt(boughtAt: ItemDto | string | null): string {
    if (typeof boughtAt === 'string') {
      return boughtAt;
    }
    return boughtAt ? boughtAt.boughtAt : '';
  }

  boughtAtSuggestions = (input: string) => (input === '')
    ? this.itemService.findAll(5)
    : this.itemService.findByBoughtAt(input);

  formatUnitName(unit: Unit | null): string {
    return unit ? unit.name : '';
  }

  toggleScanning() {
    this.scanner.isStart ? this.scanner.stop() : this.scanner.start()
  }

  updateEAN(ean: ScannerQRCodeResult[]) {
    this.scanner.pause();
    this.item.ean = this.scanner.data.value[0].value;

    this.searchForEan(this.item.ean);
  }

  searchForEan(ean: string) {
    if (ean.length === 13) {
      this.notification.info("Fetching barcode data...", "Fetching data");
      let o = this.openFoodFactService.findByEan(ean);
      o.subscribe({
        next: data => {
          this.notification.success("EAN data successfully retrieved.", "Success");
          if (data != null) {
            this.item = {
              ...this.item,
              generalName: data.generalName,
              productName: data.productName,
              brand: data.brand,
              ingredients: data.ingredients,
              quantityTotal: data.quantityTotal,
              unit: (this.availableUnits[0] == null ? this.item.unit : this.availableUnits[0]),
              ean: ean
            };
          } else {
            this.notification.warning("No data found for EAN number.", "No Data");
          }
        },
        error: error => {
          this.errorHandler.handleErrors(error, "EAN", "fetched");
        }
      })
    }
  }

  togglePlayPause() {
    if (this.scanner.isPause) {
      this.scanner.play();
    } else {
      this.scanner.pause();
    }
  }

  public delete() {
    this.itemService.deleteItem(this.item.itemId).subscribe({
      next: () => {
        this.router.navigate(['/digital-storage/']);
        this.notification.success(`Item ${this.item.productName} was successfully deleted.`, "Success");
      },
      error: error => {
        this.errorHandler.handleErrors(error, "item " + this.item.productName, "deleted");
      }
    });
  }

  getIdFormatForDeleteModal(item: ItemDto): string {
    return `${item.productName}${item.itemId.toString()}`.replace(/[^a-zA-Z0-9]+/g, '');
  }

  public compareUnitObjects(itemUnit: Unit, availableUnit: Unit): boolean {
    return itemUnit && availableUnit ? itemUnit.name === availableUnit.name : itemUnit === availableUnit;
  }
}
