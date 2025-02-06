import {Component, Input, OnInit} from '@angular/core';
import {RecipeSuggestion} from "../../../dtos/cookingDtos/recipeSuggestion";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Router} from "@angular/router";
import {CookingService} from "../../../services/cooking.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-cookbook-modal',
  templateUrl: './cookbook-modal.component.html',
  styleUrls: ['./cookbook-modal.component.scss']
})
export class CookbookModalComponent implements OnInit{

  @Input() recipe: RecipeSuggestion;
  recipeWithMissing : RecipeSuggestion;

  constructor(public activeModal: NgbActiveModal, private router: Router, public cookingService: CookingService, private notification: ToastrService) {

  }

  edit() {
    this.activeModal.dismiss();
    this.router.navigate(['cookbook/' + this.recipe.id + '/edit']);
  }

  ngOnInit(): void {
    this.cookingService.getMissingIngredients(this.recipe.id).subscribe({
      next: res => {
        this.recipeWithMissing = res;
        console.log(this.recipeWithMissing)
      },
      error: err => {
        console.error("Error loading recipes:", err);
        this.notification.error("Error loading recipes");
      }
    })
  }

  cook(){
    this.activeModal.dismiss();
    this.cookingService.cookRecipe(this.recipeWithMissing).subscribe({
      next: res => {
        console.log("cooked");
        this.notification.success(`Recipe ${this.recipeWithMissing.title} successfully cooked.`, "Success");

      },
      error: err => {
        console.error("Error loading recipes:", err);
        this.notification.error("Error loading recipes");
      }
    })
  }

  addToShoppingList() {
    this.activeModal.dismiss();
    this.cookingService.addToShoppingList(this.recipeWithMissing).subscribe({
      next: res => {
        console.log("added to list");
        this.notification.success("Ingredients added successfully to shopping list")

      },
      error: err => {
        console.error("Error loading recipes:", err);
        this.notification.error("Error loading recipes");
      }
    })
  }

}
