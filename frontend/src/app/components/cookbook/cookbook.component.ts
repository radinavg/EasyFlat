import {Component, OnInit} from '@angular/core';
import {RecipeSuggestion} from "../../dtos/cookingDtos/recipeSuggestion";
import {CookingService} from "../../services/cooking.service";
import {ToastrService} from "ngx-toastr";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {CookbookModalComponent} from "./cookbook-modal/cookbook-modal.component";
import {CookbookDetailComponent} from "./cookbook-detail/cookbook-detail.component";

@Component({
  selector: 'app-cookbook',
  templateUrl: './cookbook.component.html',
  styleUrls: ['./cookbook.component.scss']
})
export class CookbookComponent implements OnInit{
  recipes: RecipeSuggestion[];

  constructor(private cookingService: CookingService,
              private notification: ToastrService, private modalService: NgbModal) {
 }

  ngOnInit(): void {
    this.cookingService.getCookbook().subscribe({
      next: res => {
      this.recipes = res;
    },
      error: err => {
      console.error("Error loading recipes:", err);
      this.notification.error("Error loading recipes");
    }
  })
  }

  openRecipeModal(recipe: RecipeSuggestion) {
    const modalRef = this.modalService.open(CookbookModalComponent, { size: 'lg' });
    modalRef.componentInstance.recipe = recipe;
  }

  openDetailModal(recipe: RecipeSuggestion) {
    const modalRef = this.modalService.open(CookbookDetailComponent, { size: 'lg' });
    modalRef.componentInstance.recipe = recipe;
  }

  handleRecipeIsCooked(recipeTitle: string) {
    this.notification.success(`Recipe ${recipeTitle} successfully cooked.`, "Success");
  }

  handleRecipeDeleted(deletedRecipe: RecipeSuggestion) {
    this.recipes = this.recipes.filter(recipe => recipe.id !== deletedRecipe.id);
  }
}
