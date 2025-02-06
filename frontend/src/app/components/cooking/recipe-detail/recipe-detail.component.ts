import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {RecipeDetailDto, RecipeSuggestion} from "../../../dtos/cookingDtos/recipeSuggestion";
import {ItemService} from "../../../services/item.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {CookingService} from "../../../services/cooking.service";
import {NgbActiveModal, NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {MatchingModalComponent} from "../matching-modal/matching-modal.component";
import {RecipeIngredient} from "../../../dtos/cookingDtos/recipeIngredient";

@Component({
  selector: 'app-recipe-detail',
  templateUrl: './recipe-detail.component.html',
  styleUrls: ['./recipe-detail.component.scss']
})
export class RecipeDetailComponent implements OnInit {
  @Input() recipeID: string;
  recipeDetail: RecipeDetailDto;
  @Output() matchingClicked: EventEmitter<string> = new EventEmitter<string>();


  constructor(
    private service: CookingService,
    private notification: ToastrService,
    private router: Router,
    private route: ActivatedRoute,
    public activeModal: NgbActiveModal,
    private modalService: NgbModal,
    private itemService: ItemService

  ) {
  }

  ngOnInit(): void {
    this.load()
  }

  load() {
    this.service.getRecipeDetails(this.recipeID).subscribe({
      next: res => {
        this.recipeDetail = res;
        console.log("Ingredients:", this.recipeDetail.extendedIngredients);
      },
      error: error => {

      }
    })

  }


  openMatchModal(ingredient: RecipeIngredient) {
    const modalRef = this.modalService.open(MatchingModalComponent, {size: 'lg'});
    modalRef.componentInstance.ingredient = ingredient;

    modalRef.componentInstance.matchingDone.subscribe(() => {
      this.load();
    });
  }

  unMatchIngredient(ingredient: RecipeIngredient) {
    console.log(ingredient);
    if (ingredient.matchedItem && ingredient.realName) {
      const realNameIndex = ingredient.matchedItem.alternativeNames.findIndex(
        (alternativeName) => alternativeName.name === ingredient.realName
      );

      if (realNameIndex !== -1) {
        console.log(ingredient.matchedItem.alternativeNames)
        console.log("Before Delete")
        ingredient.matchedItem.alternativeNames.splice(realNameIndex, 1);
      }


      ingredient.matched = false;
      console.log(ingredient.matchedItem.alternativeNames)
      console.log("After Delete")
      // Save the updated matchedItem (assuming you have a method for updating items)

      this.itemService.updateItem(ingredient.matchedItem).subscribe({
        next: () => {
          this.notification.success(`Ingredient ${ingredient.name} successfully unmatched`, "Success");
          this.load()
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
  }


}
