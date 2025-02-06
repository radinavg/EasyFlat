import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {RecipeIngredient} from "../../../dtos/cookingDtos/recipeIngredient";
import {AlternativeName, ItemDto} from "../../../dtos/item";
import {map, Observable, of} from "rxjs";
import {CookingService} from "../../../services/cooking.service";
import {ItemService} from "../../../services/item.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-matching-modal',
  templateUrl: './matching-modal.component.html',
  styleUrls: ['./matching-modal.component.scss']
})
export class MatchingModalComponent implements OnInit {
  @Input() ingredient: RecipeIngredient;
  @Output() matchingDone: EventEmitter<void> = new EventEmitter<void>();
  availableItems: ItemDto[];
  selectedItem: ItemDto;

  constructor(public activeModal: NgbActiveModal,
              private itemService: ItemService,
              private notification: ToastrService,) {
  }

  ngOnInit(): void {
  }


  match(): void {
    console.log(this.selectedItem)
    const newAlternativeName: AlternativeName = {id: null, name: this.ingredient.name};
    this.selectedItem.alternativeNames.push(newAlternativeName)
    console.log(this.selectedItem.alternativeNames)
    this.itemService.updateItem(this.selectedItem).subscribe({
      next: () => {
        this.notification.success(`Ingredient ${this.ingredient.name} successfully matched to Item ${this.selectedItem.productName}.`, "Success");
        this.activeModal.dismiss();
        this.matchingDone.emit();
      },
      error: error => {
        console.error(`Error item was not matched: ${error}`);
        console.error(error);
        let firstBracket = error.error.indexOf('[');
        let lastBracket = error.error.indexOf(']');
        let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
        let errorDescription = error.error.substring(0, firstBracket);
        errorMessages.forEach((message: string) => {
          this.notification.error(message, errorDescription);
        });
      }
    });
  }


  formatProductName(item: ItemDto | null): string {
    return item ? item.productName : '';
  }

  nameSuggestions = (input: string): Observable<any[]> => {
    if (!input.trim()) {
      return of([]); // Return an empty array if the input is empty or contains only whitespaces
    }

    const suggestions$ = this.itemService.findByName(input,this.ingredient.unitEnum.name);

    return suggestions$.pipe(
      map(suggestions => Array.isArray(suggestions) ? suggestions : [suggestions])
    );
  }


}
