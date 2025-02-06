import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {RecipeSuggestion} from "../../../dtos/cookingDtos/recipeSuggestion";
import {CookingService} from "../../../services/cooking.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {error} from "@angular/compiler-cli/src/transformers/util";



@Component({
  selector: 'app-recipe-card',
  templateUrl: './recipe-card.component.html',
  styleUrls: ['./recipe-card.component.scss']
})
export class RecipeCardComponent implements OnInit {

  @Input() recipe: RecipeSuggestion;
  @Output() recipeAddedToCookbook: EventEmitter<string> = new EventEmitter();
  @Output() cookClicked: EventEmitter<RecipeSuggestion> = new EventEmitter<RecipeSuggestion>();
  @Output() detailsClicked: EventEmitter<string> = new EventEmitter<string>();
  @Output() recipeCooked: EventEmitter<string> = new EventEmitter();
  isSaveButtonDisabled = false;
  recipeID: string;

  constructor(
    private cookingService: CookingService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {


  }


  getTruncated(text:string,maxLength:number): string {
    return this.recipe.summary.length > maxLength ?
      text.slice(0, maxLength) + '...' :
      text;
  }

  addToCookBook() {
    this.isSaveButtonDisabled = true;
    console.log(this.recipeID)
    this.cookingService.createCookbookRecipe(this.recipe).subscribe({
      next: data => {

        this.recipeAddedToCookbook.emit(this.recipe.title);
        console.log(this.recipeID)
      },
      error: error => {
        console.error(`Error ${error}`);
        let firstBracket = error.error.indexOf('[');
        let lastBracket = error.error.indexOf(']');
        let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
        let errorDescription = error.error.substring(0, firstBracket);
        errorMessages.forEach(message => {
          this.notification.error(message, errorDescription);
        });
      }
    });
  }

  cook() {
    console.log(this.recipeID)
    this.cookingService.getMissingIngredients(this.recipeID).subscribe({
      next: (missingIngredients: RecipeSuggestion) => {
        console.log(missingIngredients)
        if (missingIngredients && missingIngredients.missedIngredients.length > 0) {
          this.recipe.id = this.recipeID;
          this.cookClicked.emit(this.recipe);
        } else {
          this.cookingService.cookRecipe(this.recipe).subscribe({
            next: res => {
              this.recipeCooked.emit(this.recipe.title);
            },
            error: error => {
              console.error('Error cooking recipe', error);
            }

          });
        }


      },
      error: error => {
        console.error('Error checking missing ingredients:', error);
      }
    });
  }

  showDetails() {
    console.log(this.recipeID + " from showDetails")
    this.detailsClicked.emit(this.recipeID);
  }

  ngOnInit(): void {
    console.log(this.recipeID + "from card (1)")
    this.recipeID = this.recipe.id;
    console.log(this.recipeID + "from card")
  }


}
