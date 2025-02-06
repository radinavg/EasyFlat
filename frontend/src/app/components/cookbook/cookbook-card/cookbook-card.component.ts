import {Component, EventEmitter, Input, Output} from '@angular/core';
import {RecipeSuggestion} from "../../../dtos/cookingDtos/recipeSuggestion";
import {CookingService} from "../../../services/cooking.service";
import {UserDetail} from "../../../dtos/auth-request";
import {Router} from "@angular/router";
import {ItemDto} from "../../../dtos/item";

@Component({
  selector: 'app-cookbook-card',
  templateUrl: './cookbook-card.component.html',
  styleUrls: ['./cookbook-card.component.scss']
})
export class CookbookCardComponent {

  @Input() recipe: RecipeSuggestion;
  @Output() cookClicked: EventEmitter<RecipeSuggestion> = new EventEmitter<RecipeSuggestion>();
  @Output() recipeCooked: EventEmitter<string> = new EventEmitter();
  @Output() recipeDeleted: EventEmitter<RecipeSuggestion> = new EventEmitter<RecipeSuggestion>();
  @Output() detailsClicked: EventEmitter<RecipeSuggestion> = new EventEmitter<RecipeSuggestion>();
  constructor(private cookingService: CookingService, private router: Router) {
  }



  getTruncatedSummary(): string {
    const maxLength = 100;
    return this.recipe.summary.length > maxLength ?
      this.recipe.summary.slice(0, maxLength) + '...' :
      this.recipe.summary;
  }

  getTruncated(text:string,maxLength:number): string {
    return this.recipe.summary.length > maxLength ?
      text.slice(0, maxLength) + '...' :
      text;
  }



  delete() {
      this.cookingService.deleteCookbookRecipe(this.recipe.id).subscribe({
        next: (deletedRecipe: RecipeSuggestion) => {
          console.log('Recipe deleted:', deletedRecipe);
          this.recipeDeleted.emit(deletedRecipe);
        },
        error: error => {
          console.error(error.message, error);
        }
      });
  }

  cook() {
    this.cookingService.getMissingIngredients(this.recipe.id).subscribe({
      next: (missingIngredients: RecipeSuggestion) => {
        if (missingIngredients && missingIngredients.missedIngredients.length > 0) {
          this.cookClicked.emit(this.recipe);
        }else {
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

  detail() {
    this.detailsClicked.emit(this.recipe);
  }

  getIdFormatForDeleteModal(recipe:RecipeSuggestion): string {

    return `${recipe.title}${recipe.id}`.replace(/[^a-zA-Z0-9]+/g, '');
  }
}
