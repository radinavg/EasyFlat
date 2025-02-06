import {Component, EventEmitter, OnInit, Output} from '@angular/core';

import {CookingService} from "../../services/cooking.service";
import {ToastrService} from "ngx-toastr";
import {RecipeSuggestion} from "../../dtos/cookingDtos/recipeSuggestion";
import {CookbookModalComponent} from "../cookbook/cookbook-modal/cookbook-modal.component";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {CookingModalComponent} from "./cooking-modal/cooking-modal.component";
import {RecipeDetailComponent} from "./recipe-detail/recipe-detail.component";


@Component({
  selector: 'app-cooking',
  templateUrl: './cooking.component.html',
  styleUrls: ['./cooking.component.scss']
})
export class CookingComponent implements OnInit {
  recipes: RecipeSuggestion[];
  empty: boolean = true;
  type: string;
  isLoading: boolean = false;
  @Output() cookClicked: EventEmitter<RecipeSuggestion> = new EventEmitter<RecipeSuggestion>();

  constructor(private cookingService: CookingService,
              private notification: ToastrService,
              private modalService: NgbModal) {


  }

  ngOnInit(): void {
this.type = "all types"
  }

  onTypeChange(): void {
    console.log(`Type changed to: ${this.type}`);
  }

  reloadRecipes() {

   this.isLoading = true

    this.cookingService.loadRecipes(this.type).subscribe({

      next: res => {
        this.recipes = res;
        this.empty = false;

        this.isLoading = false;
      },
      error: err => {

        this.isLoading = false;
        let firstBracket = err.error.indexOf('[');
        let lastBracket = err.error.indexOf(']');
        let errorMessages = err.error.substring(firstBracket + 1, lastBracket).split(',');
        let errorDescription = err.error.substring(0, firstBracket);
        errorMessages.forEach(message => {
          this.notification.error(message, errorDescription);
        });
      },

    })
  }


  openRecipeModal(recipe: RecipeSuggestion) {
    const modalRef = this.modalService.open(CookingModalComponent, {size: 'lg'});
    console.log(recipe + "from Modal");
    modalRef.componentInstance.recipe = recipe;
  }

  openDetailModal(recipeID: string) {
    const modalRef = this.modalService.open(RecipeDetailComponent, {size: 'lg'});
    console.log(recipeID + "from openDetailModal")
    modalRef.componentInstance.recipeID = recipeID;
  }

  handleRecipeAddedToCookbook(recipeTitle: string) {
    this.notification.success(`Recipe ${recipeTitle} successfully added to the cookbook.`, "Success");
  }

  makeEmptyTrue(){
    this.empty = true;
  }
}
